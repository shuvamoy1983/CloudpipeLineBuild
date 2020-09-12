ARG SPARK_IMAGE=gcr.io/spark-operator/spark:v2.4.5

FROM ${SPARK_IMAGE}

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get -y update \
 && apt-get -y upgrade \
 && apt-get install -y --no-install-recommends apt-utils \
 && apt-get install -y  wget

ENV HADOOP_VERSION 2.7.3

user root
RUN mkdir -p /opt/spark/conf
RUN apt update && apt install -y curl 

WORKDIR /opt/spark
RUN curl https://archive.apache.org/dist/hadoop/core/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz \
        | tar xvz -C /opt/  \
        && ln -s /opt/hadoop-$HADOOP_VERSION /opt/hadoop

RUN ln -s /opt/hadoop/share/hadoop/tools/lib/hadoop-aws* /opt/hadoop/share/hadoop/common/lib && \
   ln -s /opt/hadoop/share/hadoop/tools/lib/aws-java-sdk* /opt/hadoop/share/hadoop/common/lib

RUN groupadd -g 1010 hadoop && \
    useradd -r -m -u 1010 -g hadoop hadoop && \
    mkdir -p /opt/hadoop/logs && \
    chown -R -L hadoop /opt/hadoop && \
    chgrp -R -L hadoop /opt/hadoop

# Set necessary environment variables. 
ENV HADOOP_HOME="/opt/hadoop"
ENV PATH="/opt/hadoop/bin:${PATH}"




RUN apt-get update \
 && apt-get install -y curl unzip \
    python3 python3-setuptools \
 && ln -s /usr/bin/python3 /usr/bin/python
 
#jaa

RUN groupadd -g 1080 spark && \
    useradd -r -m -u 1080 -g spark spark && \
    chown -R -L spark /opt/spark && \
    chgrp -R -L spark /opt/spark


USER spark
WORKDIR /home/spark

# Set necessary environment variables. 
ENV SPARK_HOME="/opt/spark"
ENV PATH="/opt/spark/bin:${PATH}"
RUN echo "export SPARK_DIST_CLASSPATH=$(hadoop classpath)" >> /opt/spark/conf/spark-env.sh


ADD  config-1.3.4.jar mysql-connector-java-5.1.26.jar spark-hadoop-cloud_2.11-2.3.2.3.1.0.6-1.jar guava-23.0.jar spark-bigquery-with-dependencies_2.11-0.14.0-beta.jar spark-sql-kafka-0-10_2.11-2.4.0.jar kafka-clients-2.2.1.jar avro-1.8.0.jar kafka_2.11-2.3.0.jar spark-avro_2.11-2.4.0.jar elasticsearch-spark-20_2.11-6.8.7.jar /opt/spark/jars/

#ADD spark-cassandra-connector_2.11-2.4.2.jar /opt/spark/jars/

ADD GCP-1.0-SNAPSHOT-jar-with-dependencies.jar /opt/spark/examples/jars/
ADD core-site.xml /opt/hadoop/etc/hadoop/
ADD gcs-connector-latest-hadoop2.jar /opt/spark/jars/

RUN chmod g+w /opt/spark/work-dir
RUN chmod +x /opt/spark/sbin/
USER ${spark_uid}
ENTRYPOINT [ "/opt/entrypoint.sh" ]
