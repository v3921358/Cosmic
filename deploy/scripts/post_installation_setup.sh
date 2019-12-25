#!/bin/bash

# Download configurations from S3
if [ "$APPLICATION_NAME" == "dietstory-game-app-dev" ]; then
	aws s3 cp s3://dietstory-api-server-assets/dev/config/dietstory_env.list dietstory_env.list
elif [ "$APPLICATION_NAME" == "dietstory-game-app-prod" ]; then
	aws s3 cp s3://dietstory-api-server-assets/prod/config/dietstory_env.list dietstory_env.list
fi 