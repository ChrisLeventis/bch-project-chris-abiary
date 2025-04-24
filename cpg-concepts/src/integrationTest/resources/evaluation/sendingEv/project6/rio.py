from flask import Flask, request, jsonify
import requests
from dotenv import load_dotenv
import os
import logging

# Load environment variables
load_dotenv()

# Initialize Flask app
app = Flask(__name__, static_url_path='', static_folder='static')

# Set up logging
logging.basicConfig(level=logging.DEBUG)

# Groq API key and URL
api_key = os.getenv("GROQ_API_KEY")
url = "https://api.groq.com/openai/v1/chat/completions"

# Set request headers
headers = {
    "Authorization": f"Bearer {api_key}",
    "Content-Type": "application/json"
}

# Function to generate response from Groq API
def generate_response(user_message, history):
    messages = [
        {
            "role": "system",
            "content": "You're Rio, a multilingual chatbot that assesses the user's language and asks if your understanding is correct. Following which you speak in their initial language and give a fun fact regarding their chosen one and ask if the user would like to learn the language interactively with you. Please be a teacher from then onwards and encourage the user to ask pronunciations, spellings, etc."
        }
    ]
    messages.extend(history)
    messages.append({"role": "user", "content": user_message})

    data = {
        "model": "llama3-8b-8192",
        "messages": messages,
        "temperature": 1,
        "max_tokens": 1024,
        "top_p": 1,
        "stream": False
    }

    logging.debug(f"Sending request to Groq API with data: {data}")

    response = requests.post(url, headers=headers, json=data)
    logging.debug(f"Response received from Groq API: {response.json()}")
    return response.json()

# Define the chat endpoint
@app.route('/chat', methods=['POST'])
def chat():
    user_message = request.json.get('message')
    history = request.json.get('history', [])
    logging.debug(f"Received message: {user_message} with history: {history}")
    response = generate_response(user_message, history)
    return jsonify(response)

# Serve the static index.html file
@app.route('/')
def index():
    return app.send_static_file('index.html')

# Run the Flask app
if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
