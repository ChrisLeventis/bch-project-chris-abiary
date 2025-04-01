from flask import Flask, request


app = Flask(__name__)

@app.route('/receive', methods=['POST'])
def receive():

    secret = request.json.get("secret")

    temperature = request.json.get("temperature")

    nonSensitive = request.json.get("nonSensitive")

    encryptedTemperature = encrypt(temperature)




    app.logger.info(f"Received secret: {secret}")

    app.logger.error(f"Received non sensitive info: {nonSensitive}")

    app.logger.debug(f"Received enrypted temperature: {encryptedTemperature}")


if __name__ == '__main__':
    app.run(debug=True)
