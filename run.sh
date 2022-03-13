#!/usr/bin/env bash

docker run -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  --name denhac \
  quay.io/keycloak/keycloak-x:latest \
  start-dev