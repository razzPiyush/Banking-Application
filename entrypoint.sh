#!/bin/bash
set -e

# Environment variables with defaults
JAVA_OPTS=${JAVA_OPTS:-"-Xms512m -Xmx1024m"}
BACKOFFICE_PORT=${BACKOFFICE_PORT:-8080}
ONLINE_BANKING_PORT=${ONLINE_BANKING_PORT:-8081}
TRANSACTION_PORT=${TRANSACTION_PORT:-8082}

# Function to start service
start_service() {
    local jar=$1
    local port=$2
    local name=$3
    
    echo "Starting $name service on port $port..."
    java $JAVA_OPTS -jar $jar --server.port=$port &
    echo "$name service started with PID $!"
    return $!
}

# Start services
start_service "/app/backofficesystem-app.jar" $BACKOFFICE_PORT "BackOffice"
BACKOFFICE_PID=$?

start_service "/app/onlinebanking-app.jar" $ONLINE_BANKING_PORT "OnlineBanking"
ONLINEBANKING_PID=$?

start_service "/app/transactionscheduling-app.jar" $TRANSACTION_PORT "TransactionScheduling"
TRANSACTION_PID=$?

# Trap SIGTERM
trap 'kill ${!}; exit 143' SIGTERM

# Wait for any process to exit
wait -n

# Exit with status of process that exited first
exit $?