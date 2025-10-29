#!/bin/bash
# Environment variables for Service1 (Jakarta EE)

# Load from config.env
source ../config.env

export SERVICE_PORT=${SERVICE1_PORT}
export CONSUL_HOST=${CONSUL_HOST}
export CONSUL_PORT=${CONSUL_PORT}
export DB_HOST=${DB_HOST}
export DB_PORT=${DB_PORT}
export DB_NAME=${DB_NAME}
export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PASSWORD}

echo "Environment variables set for Service1"
