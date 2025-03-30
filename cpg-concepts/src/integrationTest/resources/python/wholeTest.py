from flask import Flask, request
from flask_sqlalchemy import SQLAlchemy
import random
import string
import requests

app = Flask(__name__)

app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///secrets.db"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
db = SQLAlchemy(app)


class Secret(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    content = db.Column(db.String(255), nullable=False)

with app.app_context():
    db.create_all()

def resourceHandler(func):
    func.isRO = True 
    return func


@resourceHandler
@app.route('/submit', methods=['GET'])
def submit_secret():

    secret = request.form.get('secret', '')

    if not secret:
        return "No secret provided!", 400

    randomoperation = ''.join(random.choices(string.ascii_letters + string.digits, k=5))
    secret = f"{secret}-{randomoperation}"


    new_entry = Secret(content=secret)
    db.session.add(new_entry)
    db.session.commit()


    app.logger.info(f"Stored secret: {secret}")


    try:
        response = requests.post("https://whatever.com", json={"secret": secret})
        app.logger.info(f"Secret sent, response: {response.status_code}")
    except requests.RequestException as e:
        app.logger.error(f"Failed to send secret: {e}")

    return f"Secret processed: {secret}"

if __name__ == '__main__':
    app.run(debug=True)
