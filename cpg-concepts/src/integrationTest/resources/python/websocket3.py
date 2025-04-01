import websocket
import json
from flask import Flask, request

app = Flask(__name__)

WS_SERVER_URL = "wss://example.com/socket"

def send_data(data):
    ws = websocket.WebSocket()
    ws.connect(WS_SERVER_URL)
    ws.send(json.dumps({"message": data}))
    ws.send(data)
    response = ws.recv()
    print("Received:", response)
    ws.close()

@app.route("/send")
def send():
    secret = request.form.get('secret', '')
    send_data(secret)


if __name__ == "__main__":
    app.run(debug=True)
