import requests
from flask import Flask, request

app = Flask(__name__)

@app.route('/submit', methods=['GET'])
def submit_secret():
    secret = request.json.get("key")
    requests.post("https://example.com", secret)