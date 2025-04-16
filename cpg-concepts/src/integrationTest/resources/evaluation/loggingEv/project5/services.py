from flask import Flask, request, jsonify
from tasks import collect_weather
from DB import get_db, init_db
import uuid
import logging
import math

app = Flask(__name__)

# Logger Configuration
logging.basicConfig(level=logging.DEBUG)

def is_valid_uuid(uuid_string):
    try:
        uuid_obj = uuid.UUID(uuid_string, version=4)
        return str(uuid_obj) == uuid_string
    except ValueError:
        return False

@app.route('/weather', methods=['POST'])
def start_collect_weather():
    user_id = request.json.get('user_id')
    city_ids = request.json.get('city_ids')


    app.logger.debug('user_id %s', user_id)
    app.logger.debug('city_ids %s', city_ids)

    if(user_id is not None and city_ids is not None and city_ids != [] and user_id != ""):
        request_id = str(uuid.uuid4())

        app.logger.debug('request_id %s', request_id)

        # Split the data into batchs 
        batches = [city_ids[i:i + 60] for i in range(0, len(city_ids), 60)]

        app.logger.debug('batches %s', str(batches))


        for batch in batches:
            collect_weather.apply_async(args=[request_id, user_id, batch, len(city_ids)], countdown=60 * batches.index(batch))

        return jsonify({'message': 'Weather data collection started', 'request_id': request_id}), 202
    else:
         return jsonify({'message': 'Missing Parameters'}), 422


@app.route('/weather/<request_id>', methods=['GET'])
def get_progress(request_id):
    if(request_id is not None and is_valid_uuid(request_id)):
        db = get_db()
        cur = db.execute('SELECT COUNT(*) AS total FROM weather_data WHERE request_id = ?', (request_id,))
        total = cur.fetchone()['total']

        
        cur = db.execute('SELECT total_cities AS total FROM requested_ID_weather WHERE request_id = ?', (request_id,))
        all_cities_requested = cur.fetchone()['total']
        
        db.close()

        if(all_cities_requested != 0):
            progress_till_now = math.floor((total/all_cities_requested))*100
        else:
            progress_till_now = 0

        app.logger.debug('PARTIAL %d', total)
        app.logger.debug('FULL %d', all_cities_requested)
        app.logger.debug('DIVISION %d',(total/all_cities_requested))
        app.logger.debug('MATH %d',math.floor((total/all_cities_requested)))
        app.logger.debug('PERCETUAL %d', progress_till_now)

        return jsonify({'progress': progress_till_now}), 200
    else:
        return jsonify({'message': 'Missing Parameters'}), 422

if __name__ == '__main__':
    init_db()
    app.run(debug=True)