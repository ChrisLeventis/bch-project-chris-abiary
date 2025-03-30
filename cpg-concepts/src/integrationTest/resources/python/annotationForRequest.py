import request

@resourceHandler
@app.route("/")
def hello():
    request.form.get("secret")
