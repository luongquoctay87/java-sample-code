# Init minikube
minikube start --driver=docker
minikube docker-env
eval $(minikube -p minikube docker-env)
minikube status
docker images

# Build docker images
mvn clean package
docker build -t authz:1.0 .

# Deploy application
kubectl apply -f helm/debug.yaml

sleep 2

# Set debugger
osascript -e 'tell app "Terminal" to do script "kubectl port-forward authz-static-0 5005:5005"'

## Publish to browser:"
kubectl port-forward service/authz-svc 7749:7749

# http://localhost:7749/swagger-ui/index.html

