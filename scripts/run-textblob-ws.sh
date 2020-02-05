#!/bin/bash

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

# This script run textblow webservice (textblob-ws) that is useful to compute polarity
# The service is required to run ComputePolarityTBWSFromStringBufferPipe
# This software has been developed by Enaitz Ezpeleta (Mondragon Unibertsitatea)

docker build -t textblob-ws:latest textblob-ws/
echo "Configure your pipe to use the URL is http://localhost/postjson"
#Running the container as root is required to map port 80 (below 1024).
#If you change the mapping defininition, please modify the sevice URL accordingly.
sudo docker run -p 80:80 textblob-ws:latest