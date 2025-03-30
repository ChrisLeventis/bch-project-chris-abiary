import request


secret = request.form.get("secret")


app.logger.info(secret)