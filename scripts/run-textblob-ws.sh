#!/bin/bash

# This script run textblow webservice (textblob-ws) that is useful to compute polarity
# The service is required to run ComputePolarityTBWSFromStringBufferPipe
# This software has been developed by Enaitz Ezpeleta (Mondragon Unibertsitatea)

docker build -t textblob-ws:latest textblob-ws/
docker run -p 80:localhost:80 textblob-ws:latest