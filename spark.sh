#!/bin/bash
export SPARK_NAMESPACE=sparktst5
export SA=spark-exes
export K8S_CACERT=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
export K8S_TOKEN=/var/run/secrets/kubernetes.io/serviceaccount/token

# Docker runtime image
export DOCKER_IMAGE=shuvamoy008/spark8s
export SPARK_DRIVER_NAME=spark-pipeline


export KafkaBootstrapIP=`kubectl get all -n kafka | grep -i mykafka-kafka-external-bootstrap | awk '{print $4}'`
export cassandraHost=`kubectl get svc -n cass-operator-system | grep -i cassandra-loadbalancer | awk '{print $4}'`
export cassandraUserName=$(kubectl -n cass-operator-system get secret cluster1-superuser -o json | jq -r '.data.username' | base64 --decode)
export cassandraPassword=$(kubectl -n cass-operator-system get secret cluster1-superuser -o json | jq -r '.data.password' | base64 --decode)
export mySQLIP=`kubectl get svc | grep -i mysql | awk '{print $4}'`
KUBERNET_IP=`kubectl exec -it deployment.apps/spark-master -n $SPARK_NAMESPACE  -- /bin/bash -c printenv | grep -i KUBERNETES_PORT_443_TCP_ADDR`
KUBERNET_IP=`echo $KUBERNET_IP | grep -Eo '([0-9]{1,3}\.){3}[0-9]{1,3}'`
K8ip="k8s://https://${KUBERNET_IP}"

port=443
echo "Running the spark command $K8ip:$port"

MySqlUserName="root"
MySqlPassword="password" 

kubectl exec -it deployment.apps/spark-master -n $SPARK_NAMESPACE -- /bin/bash -c "/opt/spark/bin/spark-submit --name sparkkafka-test123 \
   --master $K8ip:$port \
  --deploy-mode cluster  \
  --class org.apache.spark.examples.MainJob  \
  --conf spark.kubernetes.driver.pod.name=$SPARK_DRIVER_NAME  \
  --conf spark.kubernetes.authenticate.subdmission.caCertFile=$K8S_CACERT  \
  --conf spark.kubernetes.authenticate.submission.oauthTokenFile=$K8S_TOKEN  \
  --conf spark.kubernetes.authenticate.driver.serviceAccountName=$SA  \
  --conf spark.kubernetes.namespace=$SPARK_NAMESPACE  \
  --conf spark.executor.instances=2  \
  --conf spark.kubernetes.container.image=$DOCKER_IMAGE  \
  --conf spark.kubernetes.container.image.pullPolicy=Always \
  local:///opt/spark/examples/jars/GCP-1.0-SNAPSHOT-jar-with-dependencies.jar $KafkaBootstrapIP $cassandraHost $cassandraUserName $cassandraPassword $mySQLIP $MySqlUserName $MySqlPassword"


