# Setup Kubernetes in Windows & Run application on k8s cluster
https://medium.com/@javatechie/kubernetes-tutorial-setup-kubernetes-in-windows-run-spring-boot-application-on-k8s-cluster-c6cab8f7de5a
https://www.baeldung.com/ops/kubernetes-helm
https://minikube.sigs.k8s.io/docs/start/
https://medium.com/@javatechie/kubernetes-tutorial-setup-kubernetes-in-windows-run-spring-boot-application-on-k8s-cluster-c6cab8f7de5a
https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/#install-kubectl-binary-with-curl-on-windows
https://medium.com/@javatechie/kubernetes-tutorial-run-deploy-spring-boot-application-in-k8s-cluster-using-yaml-configuration-3b079154d232



curl.exe -LO "https://dl.k8s.io/release/v1.28.1/bin/windows/amd64/kubectl.exe"
curl.exe -LO "https://dl.k8s.io/v1.28.1/bin/windows/amd64/kubectl.exe.sha256"
kubectl version --client --output=yaml
winget install -e --id Kubernetes.kubectl
kubectl config view


