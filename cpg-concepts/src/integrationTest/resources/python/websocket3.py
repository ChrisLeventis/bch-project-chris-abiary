import websocket
import json
import threading
from flask import Flask

app = Flask(__name__)

WS_SERVER_URL = "wss://example.com/socket"

def send_data_async(data):
    def run():
        ws = websocket.WebSocket()
        ws.connect(WS_SERVER_URL)
        ws.send(json.dumps(data))
        response = ws.recv()  # Optional: handle response
        print("Received:", response)
        ws.close()

    thread = threading.Thread(target=run)
    thread.start()

@app.route("/send")
def send():
    data = {"message": "Hello from Flask!"}
    send_data_async(data)
    return "WebSocket data sent!"

if __name__ == "__main__":
    app.run(debug=True)
