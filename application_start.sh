#!/bin/bash

# Fetch secrets from AWS Secrets Manager
secret_arn="arn:aws:secretsmanager:eu-west-1:037680133179:secret:SIEnvSecrets-1lPfo9"
aws_region="eu-west-1"

echo "[Fetching Secrets] Fetching secrets from AWS Secrets Manager..."
secrets=$(aws secretsmanager get-secret-value --secret-id $secret_arn --region $aws_region --output json | jq -r ".SecretString")

# Print the fetched secrets
echo "[Fetched Secrets] Fetched secrets:"
echo "$secrets"

# Export secrets as environment variables
while IFS= read -r line; do
  export "$line"
done <<< "$(echo "$secrets" | jq -r "to_entries|map(\"\(.key)=\(.value|tostring)\")|.[]")"

# Move to the application directory
cd /home/iconnect/iconnect-main/

# Start Docker Compose
echo "[Docker Compose] Building Docker images and starting containers..."
docker-compose up --build -d

# Other tasks related to starting your application
# ...

# Continue with your existing startup logic (if not included in this script)
# exec /path/to/your_existing_start_application.sh
