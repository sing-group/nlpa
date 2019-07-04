#!flask/bin/python

#Author: Enaitz Ezpeleta (Mondragon Unibertsitatea)

from flask import Flask
from flask import request
from flask import json
from flask import Response
from textblob import TextBlob

app = Flask(__name__)

@app.route('/postjson', methods=['POST'])
def post():
    if request.is_json:
        print(request.is_json)
        content = request.get_json()
        tb = TextBlob(content['text'])
        data = { 'polarity' : tb.polarity, 'subjectivity': tb.subjectivity }
        js = json.dumps(data)
        resp = Response(js, status=200, mimetype='application/json')
        return resp
    else:
        print("No json data found")
        return 'error'

app.run(host='0.0.0.0', port=80)