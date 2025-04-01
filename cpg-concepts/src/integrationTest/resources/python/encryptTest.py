from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

FAKE_ENDPOINT = "https://httpbin.org/post"

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

    lastname = request.json.get("lastname")

    username = request.json.get("username")

    temperature = request.json.get("temperature")

    if not secret:
        return jsonify({"error": "No secret provided"}), 400

    encryptedLastname = encrypt(lastname)

    encryptedTemperature = encrypt(temperature)


    response = requests.post(FAKE_ENDPOINT, json={"forwarded_secret": secret})

    response = requests.post(FAKE_ENDPOINT, json={"forwarded_secret": encryptedLastname})

    response = requests.post(FAKE_ENDPOINT, json={"forwarded_secret": username})

    send_data(temperature)

    send_data(encryptedTemperature)



    return jsonify({"status": "Sent", "response": response.json()}), response.status_code

if __name__ == "__main__":
    app.run(debug=True)