FROM debian:9

MAINTAINER Enaitz Ezpeleta (Mondragon Unibertsitatea)

RUN apt-get update && apt-get -y install python python-pip && \
    pip install Flask && pip install -U textblob 

RUN python -m textblob.download_corpora

ADD app.py /usr/bin/

EXPOSE 80

CMD python /usr/bin/app.py