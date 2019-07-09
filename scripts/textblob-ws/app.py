#!flask/bin/python

#  NLPA
# 
#  Copyright (C) 2018 - 2019 SING Group (University of Vigo)
# 
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as
#  published by the Free Software Foundation, either version 3 of the
#  License, or (at your option) any later version.
# 
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public
#  License along with this program.  If not, see
#  <http://www.gnu.org/licenses/gpl-3.0.html>.
 
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