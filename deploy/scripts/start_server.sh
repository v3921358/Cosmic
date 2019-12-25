#!/bin/bash

SERVER_NAME=dietstory-game-server
SERVER_PATH=/apps/dietstory-server

source ~/.bashrc

# Run Server
sudo docker run -it -d \
	-v ${SERVER_PATH}:/mnt \
	-p 7575:7575 -p 7576:7576 -p 7577:7577 -p 8484:8484 \
	-e SERVER_HOST=${SERVER_HOST} \
	--env-file ${SERVER_PATH}/dietstory_env.list \
	--name=${SERVER_NAME} \
	benjixd/dietstory