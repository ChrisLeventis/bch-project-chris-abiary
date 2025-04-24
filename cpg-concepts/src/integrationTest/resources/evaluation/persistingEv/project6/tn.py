import os
from flask import Flask, request, render_template
from flask.ext.sqlalchemy import SQLAlchemy
app = Flask(__name__)

app.config["SQLALCHEMY_DATABASE_URI"] = os.environ['DATABASE_URL']

db =SQLAlchemy(app)


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    first_name = db.Column(db.Text)
    last_name = db.Column(db.Text)
    email = db.Column(db.Text, unique=True)
    password = db.Column(db.Text)
    def __string__(self):
        return self.first_name
    def __repr__(self):
        return self.first_name

@app.route('/signup.html', methods=["GET", "POST"])
def hello_world():
    if request.method == "POST":
        if not User.query.filter_by(email=request.form.get("email")).first():
            u=User(email=request.form.get("email"),
        first_name=request.form.get("first_name"),
        last_name=request.form.get("last_name"),
        password=request.form.get("password"))
            db.session.add(u)
            db.session.commit()
    return render_template('signup.html')


# @app.route('/signin.html', methods=["GET"])
# def signin():
#     return render_template('signin.html')

@app.route('/index.html', methods=["GET"])
def home():
    return render_template('index.html')

@app.route('/about.html', methods=["GET"])
def about():
    return render_template('about.html')

@app.route('/services.html', methods=["GET"])
def services():
    return render_template('services.html')

@app.route('/contact.html', methods=["GET"])
def contact():
    return render_template('contact.html')

@app.route('/blog.html', methods=["GET"])
def blog():
    return render_template('blog.html')

if __name__ == '__main__':
    app.run(debug=True)
