# NOTE:
At this point commit after: b711d87c057be57c273ff1f72d3bc813d3a1c0e3 the system does not run reliably from scratch. 
- add the services to intelliJ.
- restart the schema 
- restart the app ## very much Gaffer taped together but works

## DOCKER
- docker-compose -f common.yml -f k_cluster_zoo.yml up --remove-orphans
- docker images
- docker image prune -a
- docker ps
- docker kill

# MAVEN
- To build microservice image without context test failing: 
```ignorelang
mvn clean install -DskipTests
```
- To change mvn version temporarily:
```ignorelang
export JAVA_HOME=~/.jdks/graalvm-ce-11
```
- *Or why not just use the maven console in intelliJ??*

## Kcat
- kcat -L -b 127.0.0.1:19092
```ignorelang
   ┌──(kali㉿kaliPerm)-[~/IdeaProjects/microservices-demo]
└─$ kcat -L -b 127.0.0.1:19092
Metadata for all topics (from broker 1: 127.0.0.1:19092/1):
 3 brokers:
  broker 2 at 127.0.0.1:29092 (controller)
  broker 1 at 127.0.0.1:19092
  broker 3 at 127.0.0.1:39092
 3 topics:
  topic "__confluent.support.metrics" with 1 partitions:
    partition 0, leader 3, replicas: 2,3,1, isrs: 3,2,1
  topic "_schemas" with 1 partitions:
    partition 0, leader 3, replicas: 1,3,2, isrs: 3,1,2
  topic "twitter-topic" with 3 partitions:
    partition 0, leader 3, replicas: 3,2,1, isrs: 3,1,2
    partition 1, leader 3, replicas: 2,1,3, isrs: 3,1,2
    partition 2, leader 3, replicas: 1,3,2, isrs: 3,1,2
```
### See messages being sent to kafka:
- kcat -C -b HOST:PORT -t TOPIC_NAME
- kcat -C -b 127.0.0.1:19092 -t twitter-topic


```yaml
% Reached end of topic twitter-topic [2] at offset 60
Q���բ��������ٶ&*threw underwent Kafka�撚�b
% Reached end of topic twitter-topic [2] at offset 61
Q�����������������#it went Java�����b
% Reached end of topic twitter-topic [1] at offset 70
Q�������������erogenous Java৩��b
% Reached end of topic twitter-topic [1] at offset 71


```

