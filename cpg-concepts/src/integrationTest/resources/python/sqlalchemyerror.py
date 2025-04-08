from flask import Flask, request
from flask_sqlalchemy import SQLAlchemy
import random
import string
import requests

app = Flask(__name__)

db = SQLAlchemy(app)

class Secret(db.Model): /////das hier caused den error ????
    id = db.Column(db.Integer, primary_key=True)
    content = db.Column(db.String(255), nullable=False)

with app.app_context():
    db.create_all()



@app.route('/submit', methods=['GET'])
def foo(app):
    secret = getSecret()
    db.session.add(secret)