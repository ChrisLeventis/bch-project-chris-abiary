import datetime
import json
import uuid

import urllib3
from flask_sqlalchemy import SQLAlchemy

from flask import Flask, jsonify, request, send_from_directory, g, make_response, render_template
from flask_restful import Resource, Api, reqparse

from flask_cors import CORS, cross_origin

from speech import recognize, pronuciation_assessment, speech_synthesis
from gpt import get_assessment, send_hello_daily, send_hello_IELTS, send_messages

app = Flask(__name__, static_folder='./dist', static_url_path='')
CORS(app)
api = Api(app)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
db = SQLAlchemy(app)

total_messages = {}


# 数据库 ORM 模型
class HistoryModel(db.Model):
    __tablename__ = 'history'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    time = db.Column(db.String(80), nullable=False)
    chat = db.relationship('ChatModel', cascade='all, delete-orphan')
    mode = db.Column(db.Integer)
    level = db.Column(db.String(128))
    # 1:日常 2:ielts-未结束 3:ielts-已结束

    def __init__(self, time, mode, level):
        self.time = time
        self.mode = mode
        self.level = level

    def to_dict(self):
        return {
            'id': self.id,
            'time': self.time,
            'mode': self.mode,
            'level': self.level,
        }


class ChatModel(db.Model):
    __tablename__ = 'chat'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    history_id = db.Column(db.Integer, db.ForeignKey('history.id', ondelete='CASCADE'))
    from_gpt = db.Column(db.Integer)
    content = db.Column(db.String(2048))
    audio = db.Column(db.String(128), nullable=True)
    score = db.Column(db.String(128), nullable=True)

    def __init__(self, history_id, from_gpt, content, audio, score=-1):
        self.history_id = history_id
        self.from_gpt = from_gpt
        self.content = content
        self.audio = audio
        if score != -1:
            self.score = score

    def to_dict(self):
        return {
            'history_id': self.history_id,
            'from_gpt': self.from_gpt,
            'content': self.content,
            'audio': self.audio,
            'score': self.score,
        }


# 资源:历史会话
class History(Resource):
    def get(self):  # 查询所有历史会话
        history = HistoryModel.query.all()
        return [i.to_dict() for i in history]

    def post(self, id):  # 建立新会话
        # try:
            current_time = datetime.datetime.now()
            sqlite_datetime = current_time.strftime("%m-%d %H:%M:%S")
            level_dict = request.form.to_dict()
            level_dict.pop('username', '')
            new = HistoryModel(
                time=sqlite_datetime,
                mode=2 if id == 2 else 1,
                level=json.dumps(level_dict),
            )

            db.session.add(new)
            db.session.commit()

            username = request.form.get('username')
            if id == 2:  # Ielts
                part1topic1, part1topic2, part2topic = level_dict['part1topic1'], level_dict['part1topic2'], level_dict['part2']
                res = send_hello_IELTS(username, new.id, part1topic1, part1topic2, part2topic, total_messages)
            else:  # Daily
                res = send_hello_daily(username, new.id, level_dict['daily'], total_messages)

            tts = speech_synthesis(res)
            new_gpt = ChatModel(
                history_id=new.id,
                from_gpt=1,
                content=res,
                audio=tts,
            )
            db.session.add(new_gpt)
            db.session.commit()

            return {
                'msg': 'success',
                'id': new.id,
            }

        # except:
        #     return {'msg': 'fail'}

    def delete(self, id):  # 删除
        to_del = HistoryModel.query.get(id)
        if to_del:
            db.session.delete(to_del)
            db.session.commit()
            return {'msg': 'success'}
        return {'msg': 'no match'}


# 资源:会话记录
class Chat(Resource):
    def get(self, id):  # 查询一次会话聊天记录
        chats = ChatModel.query.filter_by(history_id=id)
        return [i.to_dict() for i in chats]

    def post(self, id):  # 更新
        try:
            history_id = request.json.get('id')
            content = request.json.get('content')
            new = ChatModel(history_id=history_id, from_gpt=0, content=content)
            db.session.add(new)
            db.session.commit()
            return {'msg': 'success'}
        except:
            return {'msg': 'fail'}


api.add_resource(History, '/history', '/history/<int:id>')
api.add_resource(Chat, '/chat/<int:id>')

@app.route('/audio/<path:filename>', methods=['GET', 'OPTIONS'])
def static_files(filename):
    if request.method == 'OPTIONS':
        response = make_response()
        response.headers['Access-Control-Allow-Origin'] = '*'
        response.headers['Access-Control-Allow-Methods'] = 'GET'
        response.headers['Access-Control-Allow-Headers'] = 'Content-Type'
        return response
    else:
        response = send_from_directory('audio', filename)
        response.headers['Access-Control-Allow-Origin'] = '*'
        return response


@app.route('/gpt/<int:id>', methods=['POST'])
def handle_recording(id):
    # print(request)
    audio_file = request.files.get('audio')
    print('get file', audio_file)

    # To Text
    path = 'audio/' + uuid.uuid4().hex + '.wav'
    audio_file.save(path)
    # print('saved' + path)
    res = recognize(path)
    # print(type(res), res)
    try:
        score = json.dumps(pronuciation_assessment(path))
    except TypeError:
        score = json.dumps({'AccuracyScore':0, 'FluencyScore':0, 'CompletenessScore':0, 'PronScore':0})

    new = ChatModel(
        history_id=id,
        from_gpt=0,
        content=res,
        audio=path,
        score=score,
    )
    db.session.add(new)
    db.session.commit()

    # Gpt Here
    reply = send_messages(id, res, total_messages)

    # To Speech
    tts = speech_synthesis(reply)
    new_gpt = ChatModel(
        history_id=id,
        from_gpt=1,
        content=reply,
        audio=tts,
    )
    db.session.add(new_gpt)
    db.session.commit()

    return {'msg': 'success'}


@app.route('/gpt/assess', methods=['POST'])
def assess():
    content = request.form.get('content')
    return {
        'msg': 'success',
        'assess': get_assessment(content),
    }


@app.route('/')
def index():
    return send_from_directory('', 'dist/index.html')


if __name__ == '__main__':
    app.run(debug=True)
