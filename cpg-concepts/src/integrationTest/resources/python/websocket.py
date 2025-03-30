from flask_socketio import SocketIO
socketio = SocketIO(app)

@socketio.on('send_data')
def handle_data(data):
    socketio.emit('external_event', data, to='external_namespace')
