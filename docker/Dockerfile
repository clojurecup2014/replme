# DOCKER-VERSION 1.2.0
# VERSION 0.1

FROM ubuntu:14.04
MAINTAINER Edward Paget <edward@cassetteta.pe>

RUN apt-get update

RUN DEBIAN_FRONTEND=noninteractive apt-get upgrade -y
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-7-jre
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y wget
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y git-core
ADD https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein /opt/lein
RUN chmod a+x /opt/lein
ADD ./repl_start.sh /repl_start.sh
RUN chmod a+x /repl_start.sh

EXPOSE 8081

ENV LEIN_ROOT yessir

RUN /opt/lein

ENTRYPOINT ["/repl_start.sh"]


