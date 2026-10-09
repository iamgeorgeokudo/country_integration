#!/usr/bin/env bash

set -Eeuo pipefail

APP_NAME="country-integration"
IMAGE="country-integration:1.0.0"

echo "=== Country Integration Kubernetes Deployment ==="

# Verify required tools
for cmd in kubectl minikube docker; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        echo "ERROR: Required command not found: $cmd"
        exit 1
    fi
done

# Verify required manifests
for file in \
    k8s/configmap.yaml \
    k8s/deployment.yaml \
    k8s/service.yaml; do
    if [[ ! -f "$file" ]]; then
        echo "ERROR: Missing manifest: $file"
        exit 1
    fi
done

# Confirm Minikube is running
if ! minikube status >/dev/null 2>&1; then
    echo "ERROR: Minikube is not healthy."
    echo "Start and verify Minikube before deploying."
    exit 1
fi

# Confirm the database credentials secret exists
if ! kubectl get secret country-integration-db \
    >/dev/null 2>&1; then
    echo "ERROR: Kubernetes database secret is missing."
    echo "Create country-integration-db before deploying."
    exit 1
fi

echo "Loading application image into Minikube..."
minikube image load "$IMAGE"

echo "Applying Kubernetes configuration..."
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

echo "Waiting for deployment rollout..."
kubectl rollout status \
    "deployment/$APP_NAME" \
    --timeout=180s

echo
echo "=== Deployment Summary ==="
kubectl get deployment "$APP_NAME"
kubectl get pods -l "app=$APP_NAME"
kubectl get service "$APP_NAME"

echo
echo "Deployment completed successfully."