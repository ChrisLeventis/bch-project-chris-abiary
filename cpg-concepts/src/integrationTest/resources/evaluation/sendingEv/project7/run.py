# -*- coding: utf-8 -*-

from flask import Flask, render_template, redirect, request
import json
import time

from dao.weiboinfo_dao import WeiboInfoDao
from util.util import TimeUtil

import requests

app = Flask(__name__)

app.jinja_env.globals.update(t_strptime=TimeUtil.t_strptime)

redirect_uri = 'http://123.56.226.235:8090/weibo/redirectURI'
auth_url = 'https://api.weibo.com/oauth2/authorize?client_id=4177092657&response_type=code&redirect_uri=' + redirect_uri
token_url = 'https://api.weibo.com/oauth2/access_token?client_id=4177092657&client_secret=1bf707cc2ae50bf6ab342987a0fb6769&grant_type=authorization_code&redirect_uri=' + redirect_uri + '&code='
weibo_info_dao = WeiboInfoDao()

@app.route('/')
def main_page():
    return render_template('index.html')

@app.route('/weibo_favorite')
def weibo_favorite():
    weibo_favorite_api = 'https://api.weibo.com/2/favorites.json?access_token={0}&count={1}&page={2}'
    weibo = weibo_info_dao.get(1)
    res = requests.get(weibo_favorite_api.format(weibo.access_token, 50, 1))

    return render_template('weibo_favorite.html', favorites=json.loads(res.text)['favorites'])

@app.route('/weibo/auth')
def weibo_auth():
    return redirect(auth_url)

@app.route('/weibo/redirectURI')
def weibo_auth_redirectURI():
    global token_url
    code = request.args.get('code')
    token_url = token_url + code

    res = requests.post(token_url)
    auth_res = json.loads(res.text)
    entity = WeiboInfo(access_token=auth_res['access_token'], \
        expires_in=auth_res['expires_in'], uid=auth_res['uid'])
    weibo_info_dao.add(entity)

    return render_template('weibo_favorite.html')


if __name__ == '__main__':
    app.run(debug=True, port=8090, host='0.0.0.0')
