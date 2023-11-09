# Init minikube
minikube start --driver=docker
minikube docker-env
eval $(minikube -p minikube docker-env)
minikube status
docker images

# build docker images
mvn clean package
docker build -t authz:1.0 .

# Deploy application
kubectl apply -f helm/.
#kubectl port-forward service/authz-svc 7749:7749

#Open browser:
# http://localhost:7749/swagger-ui/index.html

