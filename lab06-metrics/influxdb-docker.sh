#!/usr/bin/env bash

# example commands if you would like the convenience of starting individual docker containers
docker run -d \
  -p 8086:8086 \
  --name influxdb-v1 \
  influxdb:1.11
