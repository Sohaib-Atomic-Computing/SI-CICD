#!/bin/bash
echo $(pwd)
echo "[Docker Compose] Building Docker images and starting containers..."
docker-compose up --build -d