import os
import json
from datetime import datetime
from flask import Flask, request, jsonify, send_from_directory, abort, redirect
from flask_cors import CORS
from dotenv import load_dotenv
import mysql.connector
from mysql.connector import Error

load_dotenv()

app = Flask(__name__, static_folder='diffQ')
CORS(app)

MYSQL_CONFIG = {
    'host': os.getenv('MYSQL_HOST'),
    'user': os.getenv('MYSQL_USER'),
    'password': os.getenv('MYSQL_PASSWORD'),
    'database': os.getenv('MYSQL_DB')
}

def init_db():
    try:
        conn = mysql.connector.connect(**MYSQL_CONFIG)
        cursor = conn.cursor()
        cursor.execute('''
                       CREATE TABLE IF NOT EXISTS responses (
                                                                id INT AUTO_INCREMENT PRIMARY KEY,
                                                                qid VARCHAR(255),
                           timestamp DATETIME,
                           name VARCHAR(255),
                           email VARCHAR(255),
                           familiarity TEXT,
                           expertise TEXT,
                           general_comments TEXT,
                           response_json JSON
                           )
                       ''')
        conn.commit()
        cursor.close()
        conn.close()
    except Error as e:
        print("Database init failed:", e)

@app.route('/submit', methods=['POST'])
def submit():
    data = request.json
    if not data:
        return jsonify({"status": "error", "message": "No data received"}), 400

    try:
        qid = data.get('qid')
        timestamp = data.get('timestamp', datetime.utcnow().isoformat())
        user = data.get('user', {})
        name = user.get('name', '')
        email = user.get('email', '')
        familiarity = user.get('familiarity_with_code_review', '')
        java_expertise = user.get('expertise_in_java', '')
        general_comments = user.get('general_comments', '')
        responses_json = json.dumps(data.get('responses', {}))

        conn = mysql.connector.connect(**MYSQL_CONFIG)
        cursor = conn.cursor()
        cursor.execute('''
                       INSERT INTO responses (qid, timestamp, name, email, familiarity, expertise, general_comments, response_json)
                       VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                       ''', (qid, timestamp, name, email, familiarity, java_expertise, general_comments, responses_json))
        conn.commit()
        cursor.close()
        conn.close()

        return jsonify({'status': 'success', 'message': 'Submission stored.'}), 200
    except Error as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/submit-feedback', methods=['POST'])
def submit_feedback():
    data = request.json
    if not data:
        return jsonify({"status": "error", "message": "No data received"}), 400

    try:
        qid = data.get('qid')
        prid = data.get('prid')
        diffid = data.get('diffid')
        preference = data.get('preference')
        comments = data.get('comments', '')

        if not all([qid, prid is not None, diffid is not None, preference]):
            return jsonify({"status": "error", "message": "Missing required fields"}), 400
        print("Received feedback data:", data)
        print(**MYSQL_CONFIG)
        print(MYSQL_CONFIG)
        conn = mysql.connector.connect(**MYSQL_CONFIG)
        cursor = conn.cursor()
        print("Submitting feedback:", qid, prid, diffid, preference, comments)

        cursor.execute('''
                       INSERT INTO feedback_responses (qid, prid, diffid, preference, comments, timestamp)
                       VALUES (%s, %s, %s, %s, %s, %s)
                       ''', (qid, prid, diffid, preference, comments, datetime.utcnow().isoformat()))

        conn.commit()
        cursor.close()
        conn.close()

        return jsonify({'status': 'success', 'message': 'Feedback submitted.'}), 200

    except Error as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500


@app.route('/', defaults={'requested_path': ''})
@app.route('/<path:requested_path>')
def serve_static(requested_path):
    full_path = os.path.join(app.static_folder, requested_path)
    if os.path.isfile(full_path):
        return send_from_directory(app.static_folder, requested_path)
    if os.path.isdir(full_path) and not request.path.endswith('/'):
        return redirect(request.path + '/')
    if os.path.isdir(full_path):
        index_path = os.path.join(full_path, 'index.html')
        if os.path.isfile(index_path):
            return send_from_directory(full_path, 'index.html')
    return abort(404)

if __name__ == '__main__':
    init_db()
    print(MYSQL_CONFIG)
    app.run(debug=True, port = 5555)
