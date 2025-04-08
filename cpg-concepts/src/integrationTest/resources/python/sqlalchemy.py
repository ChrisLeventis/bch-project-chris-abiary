from flask import Flask, request
from flask_sqlalchemy import SQLAlchemy
import random
import string
import requests

app = Flask(__name__)

db = SQLAlchemy(app)

def foo(app):
    secret = getSecret()

    db.session.add(42)
