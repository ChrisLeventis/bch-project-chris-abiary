from flask import Flask, request, render_template


app = Flask(__name__)

@app.route("/")
def showForm():
    return render_template("textform.html")

@app.route("/insert", methods=["POST"])
def saveToFile():
    secretData = request.form.get("secret")
    with open("data.txt", "a") as file:
        file.write(f"{secretData}\n")
        return "Done"

if __name__ == '__main__':
    app.run(debug=True)
