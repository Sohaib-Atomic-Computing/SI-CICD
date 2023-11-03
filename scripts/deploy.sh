#!/bin/bash
echo "[Docker Compose] Building Docker images and starting containers..."
docker-compose -f /home/iconnect/iconnect-main/docker-compose.yml up --build -d