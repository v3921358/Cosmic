#!/bin/bash

SERVER_NAME=dietstory-game-server
SERVER_PATH=/apps/dietstory-server

if [ "$APPLICATION_NAME" == "dietstory-game-app-dev" ]; then
	CPUS=1
	CPU_SHARES=1024
elif [ "$APPLICATION_NAME" == "dietstory-game-app-prod" ]; then
	CPUS=2
	CPU_SHARES=2048	
fi

source ~/.bashrc

# Run Server
sudo docker run -it -d \
	-v ${SERVER_PATH}:/mnt \
	-p 7575:7575 -p 7576:7576 -p 7577:7577 -p 8484:8484 \
	-e SERVER_HOST=${SERVER_HOST} \
	--env-file ${SERVER_PATH}/dietstory_env.list \
	--name=${SERVER_NAME} \
	--cpus=${CPUS} \
	--cpu-shares=${CPU_SHARES} \
	benjixd/dietstory:java_8