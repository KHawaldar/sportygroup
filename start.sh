#!/bin/bash

ENV=$1

if [ -z "$ENV" ]; then
  echo "Usage: $0 {local|prod|qa}"
  exit 1
fi

ENV_FILE=".env.$ENV"

if [ ! -f "$ENV_FILE" ]; then
  echo "Environment file $ENV_FILE does not exist!"
  exit 1
fi

echo "add a stop first to avoid conflicts"
docker-compose --env-file "$ENV_FILE" down

echo "Starting docker-compose with environment file $ENV_FILE"
docker-compose --env-file "$ENV_FILE" up --build -d