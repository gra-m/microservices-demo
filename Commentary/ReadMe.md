# NOTE:

At this point commit after: b711d87c057be57c273ff1f72d3bc813d3a1c0e3 the system does not run reliably from scratch.

- add the services to intelliJ.
- restart the schema
- restart the app ## very much Gaffer taped together but works

## CURL

- check that config-data is serving on 8888 when that port has been opened in docker file

```ignorelang
┌──(kali㉿kaliPerm)-[~/IdeaProjects/microservices-demo/config-server-repository]
└─$ curl http://localhost:8888/actuator/health                         
{"status":"UP"}   

```

## DOCKER

- docker-compose -f common.yml -f k_cluster_zoo.yml up --remove-orphans
- docker images
- docker image prune -a
- docker ps
- docker kill
- docker system prune -f
- docker system prune -a == clean start

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

## ElasticSearch Docker cluster
```ignorelang
linux /etc/sysctl.conf add: 
vm.max_map_count = 262144

or amount required in error message
```

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
# Reinstalling docker
```ignorelang
    ┌──(kali㉿kaliPerm)-[~/IdeaProjects/microservices-demo/docker-compose]
└─$ dpkg -l | grep -i docker          
ii  docker-buildx-plugin                   0.11.1-1~debian.11~bullseye          amd64        Docker Buildx cli plugin.
ii  docker-ce                              5:24.0.4-1~debian.11~bullseye        amd64        Docker: the open-source application container engine
ii  docker-ce-cli                          5:24.0.4-1~debian.11~bullseye        amd64        Docker CLI: the open-source application container engine
ii  docker-ce-rootless-extras              5:24.0.4-1~debian.11~bullseye        amd64        Rootless support for Docker.
ii  docker-compose-plugin                  2.19.1-1~debian.11~bullseye          amd64        Docker Compose (V2) plugin for the Docker CLI.
rc  docker.io                              20.10.24+dfsg1-1+b3                  amd64        Linux container runtime
 
 ┌──(kali㉿kaliPerm)-[~/IdeaProjects/microservices-demo/docker-compose]
└─$ sudo apt-get purge -y docker-buildx-plugin   

for all of the above
then do this for
sudo rm -rf /etc/docker
/var/lib/docker
and
/var/run/docker.sock

Then remove the Network interface 
sudo ifconfig docker0 down

and Ethernet Bridge
brctl delbr docker0
                                                                                                                     

```

## Reinstall
```ignorelang
sudo apt update

sudo apt install -y docker.io

sudo systemctl enable docker --now

```


  


# Issues Solved

- 33. Typos found project is now ignoring Autowire issues because of issue with KafkaListenerEndpointRegistry
Then this issue with docker:
```ignorelang
      └─$ docker compose up                 
[+] Running 6/0
 ✘ kafka-broker-1 Error                                                                                                                                                                                                           0.0s 
 ✘ kafka-broker-3 Error                                                                                                                                                                                                           0.0s 
 ✘ config-server Error                                                                                                                                                                                                            0.0s 
 ✘ schema-registry Error                                                                                                                                                                                                          0.0s 
 ✘ kafka-broker-2 Error                                                                                                                                                                                                           0.0s 
 ✘ zookeeper Error                                                                                                                                                                                                                0.0s 
Error response from daemon: Get "https://com.microservices.demo/v2/": dial tcp: lookup com.microservices.demo on 192.168.178.1:53: no such host

```

