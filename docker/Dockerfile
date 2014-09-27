# DOCKER-VERSION 1.2.0
# VERSION 0.1

FROM ubuntu:14.04
MAINTAINER Edward Paget <edward@cassetteta.pe>

RUN apt-get update

RUN DEBIAN_FRONTEND=noninteractive apt-get upgrade -y
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-7-jre
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y wget
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein /opt/lein
RUN chmod a+x /opt/lein

EXPOSE 8081

ENV LEIN_ROOT yessir

RUN /opt/lein

CMD ["/opt/lein", "repl", ":headless", ":host", "0.0.0.0", ":port", "8081"]


