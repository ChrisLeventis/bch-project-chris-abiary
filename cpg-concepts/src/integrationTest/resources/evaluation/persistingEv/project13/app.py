from flask import Flask, request, jsonify, send_from_directory, render_template
import os
import subprocess

app = Flask(__name__)

# Path to the data file
DATA_FILE = "data.txt"

# Serve the index route (welcome message)
@app.route('/')
def index():
    return render_template('index.html')

# Serve favicon.ico to avoid 404 error for favicon requests
@app.route('/favicon.ico')
def favicon():
    return send_from_directory(os.path.join(app.root_path, 'static'), 'favicon.ico')

# Route to save input to data.txt
@app.route('/save', methods=['POST'])
def save_to_file():
    data = request.json.get("input", "")
    if data:
        with open(DATA_FILE, "w") as f:
            f.write(data)
        return jsonify({"message": "Input saved to data.txt"}), 200
    return jsonify({"error": "No input provided"}), 400

# Route to analyze the data and return output
@app.route('/analyze', methods=['POST'])
def analyze():
    try:
        # Check if the data file exists
        if not os.path.exists(DATA_FILE):
            return jsonify({"error": "Data file not found. Please save input first."}), 400
        
        # Run the grading Python script
        result = subprocess.check_output(['python', 'grading_script.py'], text=True)
        return jsonify({"output": result.strip()}), 200
    except subprocess.CalledProcessError as e:
        return jsonify({"error": f"Error running script: {e.output}"}), 500
    except FileNotFoundError:
        return jsonify({"error": "grading_script.py not found"}), 500
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5003)
