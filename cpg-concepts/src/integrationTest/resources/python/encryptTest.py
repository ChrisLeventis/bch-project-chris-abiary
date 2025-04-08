from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

FAKE_ENDPOINT = "https://example.com/post"

WS_SERVER_URL = "wss://example.com/socket"

def send_data(data):
    ws = websocket.WebSocket()
    ws.connect(WS_SERVER_URL)
    ws.send(data)
    response = ws.recv()
    print("Received:", response)
    ws.close()

@app.route("/receive", methods=["POST"])
def receive_data():

    secret = request.json.get("secret")
    temperature = request.json.get("temperature")
    nonSensitive = request.json.get("nonsensitive")

    encryptedTemperature = encrypt(temperature)

    response = requests.post(FAKE_ENDPOINT, json={"forwarded_data": secret})
    response = requests.post(FAKE_ENDPOINT, json={"forwarded_data": encryptedTemperature})
    response = requests.post(FAKE_ENDPOINT, json={"forwarded_data": nonSensitive})

    send_data(secret)
    send_data(encryptedTemperature)
    send_data(nonSensitive)

    return "Data sent successfully", 200


if __name__ == "__main__":
    app.run(debug=True)