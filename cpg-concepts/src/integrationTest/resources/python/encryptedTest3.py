from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy


app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)


class Secret(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    value = db.Column(db.String(255), nullable=False)

class Temperature(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    value = db.Column(db.String(255), nullable=False)

class NonSensitive(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    value = db.Column(db.String(255), nullable=True)


@app.route('/receive', methods=['POST'])
def receive():

    secret = request.json.get("secret")

    temperature = request.json.get("temperature")

    nonsensitive = request.json.get("nonSensitive")


    encryptedTemperature = encrypt(temperature)



    new_secret = Secret(value = secret)
    new_temperature = Temperature(value = encryptedTemperature)
    new_nonsensitive = NonSensitive(value = nonsensitive)

    db.session.add(new_secret)
    db.session.add(new_temperature)
    db.session.add(new_nonsensitive)
    db.session.commit()

    return jsonify({"message": "Data stored successfully"}), 201

if __name__ == '__main__':
    with app.app_context():
        db.create_all()  # Create tables if they don't exist
    app.run(debug=True)
