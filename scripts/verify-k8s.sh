#!/usr/bin/env bash

set -Eeuo pipefail

APP_NAME="country-integration"
SERVICE_NAME="country-integration"

echo "=== Country Integration Verification ==="

echo
echo "1. Checking Kubernetes node..."
kubectl get nodes

echo
echo "2. Checking deployment..."
kubectl rollout status \
    "deployment/$APP_NAME" \
    --timeout=120s

echo
echo "3. Checking application pods..."
kubectl get pods -l "app=$APP_NAME"

echo
echo "4. Checking service..."
kubectl get service "$SERVICE_NAME"

echo
echo "5. Checking application health..."

PF_PID=""

cleanup() {
    if [[ -n "$PF_PID" ]]; then
        kill "$PF_PID" 2>/dev/null || true
    fi
}

trap cleanup EXIT

kubectl port-forward \
    "service/$SERVICE_NAME" 18085:8085 \
    >/tmp/country-integration-port-forward.log 2>&1 &

PF_PID=$!

for attempt in $(seq 1 20); do
    if ! kill -0 "$PF_PID" 2>/dev/null; then
        echo "ERROR: Port forwarding failed."
        cat /tmp/country-integration-port-forward.log
        exit 1
    fi

    if curl -fsS \
        http://127.0.0.1:18085/actuator/health \
        >/dev/null; then
        break
    fi

    if [[ "$attempt" -eq 20 ]]; then
        echo "ERROR: Application health check failed."
        cat /tmp/country-integration-port-forward.log
        exit 1
    fi

    sleep 2
done

curl -fsS http://127.0.0.1:18085/actuator/health
echo

echo
echo "All verification checks passed."