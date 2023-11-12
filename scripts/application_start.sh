#!/bin/bash
echo "[Docker Compose] Building Docker images and starting containers..."
echo $(pwd)
docker-compose up --build -d