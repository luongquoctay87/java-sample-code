# Init minikube
minikube start --driver=docker
minikube docker-env
eval $(minikube -p minikube docker-env)
minikube status
docker images

# build docker images
mvn clean package -DskipTests
docker build -t account:1.0 .

# Deploy application
kubectl apply -f helm/postgres-config.yaml
kubectl apply -f helm/postgres-pvc-pv.yaml
kubectl apply -f helm/postgres-deployment.yaml
kubectl apply -f helm/postgres-service.yaml
kubectl apply -f helm/account-deployment.yaml
kubectl apply -f helm/account-service.yaml

