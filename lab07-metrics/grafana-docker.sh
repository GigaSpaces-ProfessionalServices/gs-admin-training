#!/usr/bin/env bash

# example commands if you would like the convenience of starting individual docker containers
docker run -d \
  --name=grafana \
  -p 3000:3000 \
  grafana/grafana
