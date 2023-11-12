#!/bin/bash
echo $(pwd)
echo "[Stop Application] Stopping Docker containers..."
docker-compose down --volumes