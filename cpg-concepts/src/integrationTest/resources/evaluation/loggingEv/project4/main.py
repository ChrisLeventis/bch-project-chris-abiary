
import crops.plants as plants
from crops.code_enums import DroughtRiskValues
from flask import (
    Flask,
    request,
    jsonify
)
from flask_cors import CORS

from utils.risk import get_drought_risk, get_mean_risk, get_recomendation, get_spt

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes



@app.route('/plants', methods=['GET'])
def get_plants():
    return jsonify(list(plants.all_plants))

@app.route("/drought-analysis", methods=["POST"])
def drought_analysis():

    data_input = {
        "crop_type": request.json.get('crop_type'),
        "latitude": request.json.get('latitude'),
        "longitude": request.json.get('longitude'),
        "radius_km": request.json.get('radius_km'),                 # radius in kilometers of the circle
        "is_irrigated": request.json.get('is_irrigated'),           # "Does it have irrigation or the capacity to invest in one?"
        "planting_period": request.json.get('planting_period'),     # "Are we before, during, or after the ideal planting period?"
        "existing_crops": request.json.get('existing_crops')        # "Is there already a crop planted at the moment?"
    }
    app.logger.debug(data_input['crop_type'])


    sc = get_drought_risk(data_input['crop_type'])
    precipitation_sum, spt = get_spt(data_input['latitude'], data_input['longitude'])
    
    su = DroughtRiskValues.VERY_LOW    
    sb = DroughtRiskValues.VERY_LOW

    mean_risk = get_mean_risk(sc.value, su.value, sb.value, spt.value)
    recomandation = get_recomendation(
                                mean_risk, 
                                data_input['is_irrigated'], 
                                data_input['planting_period'],
                                data_input['existing_crops']
                                )

    output = {
        "precipitation_mean": precipitation_sum,
        "sc": sc.name,                                       # sucetibilidade da cultura (quanto maior, maior probabilidade de perdas na procução)
        "su": su.name,                                       # sucetibilidade da umidade (quanto maior, maior probabilidade de perdas na procução)
        "sb": sb.name,                                       # sucetibilidade do balanço hidrigo (quanto maior, maior probabilidade de perdas na procução)
        "spt": spt.name,                                     # sucetibilidade do previsão de chuvas (quanto maior, maior probabilidade de perdas na procução)
        "recomentation": recomandation
    }
    return jsonify(output)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)
