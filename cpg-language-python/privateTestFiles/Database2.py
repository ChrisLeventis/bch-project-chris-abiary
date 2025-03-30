from flask import Flask, jsonify, request, render_template
import psycopg2
from psycopg2.extensions import connection


app = Flask(__name__)


def connectionDB() -> connection:
    connection=psycopg2.connect(
        dbname='datenbankaisec',
        user='u1_1',
        host='localhost',
        port=5432
    )
    return connection


@app.route("/")
def showForm():
    return render_template("textform.html")


@app.route("/insert", methods=["POST"])
def insert():
    #data = request.get_json()
    #name = data.get("name")
    secret = request.form.get("secret")

    #potential processing

    connection = connectionDB()     #no database object through flask, problem?
    cursor = connection.cursor()
    try:
        cursor.execute('INSERT INTO users (name) VALUES (%s);', (secret,))
    except Exception:
        connection.rollback()
    finally:
        cursor.close()
        connection.close()

    return jsonify({'message': 'success'})

"""
curl -X POST http://127.0.0.1:5000/insert -H "Content-Type: application/json" -d '{"name": "chris"}'
"""

if __name__ == "__main__":
    app.run(debug=True)