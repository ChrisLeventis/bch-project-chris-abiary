from flask import Flask, render_template, request, session
from flask_session import Session


app = Flask(__name__)

app.config["SESSION_TYPE"] = "filesystem"
app.config['SESSION_SERIALIZER'] = 'json'
Session(app)


@app.route("/")
def showHtml():
    return render_template("textform.html")

@app.route("/insert", methods=["POST"])
def sessionInsert():
    secret = request.form.get("secret")

    # processing??

    session["secret"] = secret
    return "done"


if __name__ == '__main__':
    app.run(debug=True)