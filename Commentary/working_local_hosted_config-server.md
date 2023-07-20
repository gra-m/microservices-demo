### Update absolute path to config-server-repository
- found in config-server/src/main/resources/bootstrap.yml
### run config-server
### run kafka broker
```
docker compose -f  common.yml -f k_cluster.yml up
```
### run twitter-to-kafka service

- lsof -i:8080 netty uses this twitter-to-k fails if in use
- lsof -i:8888 configs are served on this

on linux to list blocked in use ports in case of conflict