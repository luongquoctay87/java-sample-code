# Init minikube
minikube start --driver=docker
minikube docker-env
eval $(minikube -p minikube docker-env)
minikube status
docker images

# Build docker images
mvn clean package
docker build -t gateway:1.0 .

# Deploy application
kubectl apply -f helm/.

# Start the tunnel to create a routable IP for the ‘gateway-svc’ deployment
minikube tunnel

# Open browser: http://127.0.0.1:4953/swagger-ui/index.html

