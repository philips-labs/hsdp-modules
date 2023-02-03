# Terraform: From Zero to Hero
This training session is going to give you an overview about Terraform features and how you can build your Infrastructure as Code. 

# Prerequisites
If you want to try out along with me during training, please make sure you install the following tools before the session:

### 1. Install multipass in your system

- Mac users, follow [this link](https://multipass.run/docs/installing-on-macos)
- Windows users, follow [this link](https://multipass.run/docs/installing-on-windows)

### 2. Create two VMs using multipass

**2.1 Create a docker VM using the following command**
```shell
# Create docker ubuntu instance
$ multipass launch --name dockerVM --cpus 2 --mem 2G --disk 5G

# List instances to verify if its created
$ multipass list
# The above command will return as follows
# Name                    State             IPv4             Image
# dockerVM                Running           192.168.64.13    Ubuntu 22.04 LTS
#                                          172.17.0.1
```
Install docker and portainer using the following steps:

1. Login to dockerVM 
```shell
# Login
$ multipass shell dockerVM

# Create a shell script
$ touch setup.sh
$ chmod u+x setup.sh
$ vim setup.sh
```
2. Copy the below script to setup.sh and save
```shell
#! /usr/bin/bash
echo "Installing docker, portainer..."

sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install build-essential -y

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
apt-cache policy docker-ce
sudo apt install docker-ce
sudo usermod -aG docker $USER && newgrp docker

sudo docker volume create portainer_data
sudo docker run -d -p 9000:9000 -p 9443:9443 --name portainer \
    --restart=always \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v portainer_data:/data \
    portainer/portainer-ce:2.16.2
```
3. Run setup.sh
```shell
$ ./setup.sh
```
4. Exit from dockerVM shell and relogin to verify the docker command works with current user
```shell
$ exit
$ multipass shell dockerVM
$ docker version
```
Now install **Terraform** in this VM by following the steps documented [for ubuntu](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)

If the last step fails, run this step instead:
```shell
# Install terraform
$ sudo snap install terraform --classic

# Verify installation
$ terraform -v
```

This docker instance is now configured with a management console by [portainer.io](https://www.portainer.io/). Open a web browser and type in `http://192.168.64.13:9000` to login to management console. Replace the IP address with the external IP address configured to dockerVM in your system

**2.2 Create a k3s VM using the following command**
```shell
# Create vanilla ubuntu instance
$ multipass launch --name k3sVM --cpus 2 --mem 4G --disk 10G

# List instances to verify if its created
$ multipass list

# Login to VM instance
$ multipass shell k3sVM

# Upgrade apt packages
$ sudo apt update
$ sudo apt upgrade

# Install k3s with write-kubeconfig-mode (This allows the current user to run kubectl commands)
$ curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644

# Verify if the kube-system namespace resources are up and running
$ kubectl get all -A
```

**2.3 Configure kubectl in dockerVM**

Follow the instructions below to setup kubectl in dockerVM and set the default context to k3VM
```shell
# Login to dockerVM from your host machine
$ multipass shell dockerVM
```
1. Install kubectl client inside dockerVM by following the instructions documented [here](https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)

2. Create kube config and open file
```shell
$ mkdir .kube
$ touch config
$ vim config
```
3. Login to k3sVM in another terminal or command prompt and copy the kube config
```shell
# Login
$ multipass shell k3sVM

# Go to config folder
$ cd /etc/rancher/k3s

# Open and copy content from k3s.yaml
$ cat k3s.yaml
```
4. Copy content from k3s.yaml and Paste to opened config file in previous terminal
5. Change server value to `https://<k3sVM_ext_IP>:6443`
6. Save and exit 
7. Verify kubectl context from dockerVM
```shell
# Get contexts
$ kubectl config get-contexts
# Command will return the below response
CURRENT   NAME      CLUSTER   AUTHINFO   NAMESPACE
*         default   default   default    
```
**2.4 Install portainer agent in k3sVM**

Now install portainer agent for Kubernetes in `k3sVM` to access the Kubernetes cluster using the management console running in `dockerVM`. Follow the steps below:
1. Navigate to [create environment page](http://192.168.64.13:9000/#!/wizard/endpoints/create?envType=kubernetes) (http://192.168.64.13:9000/#!/wizard/endpoints/create?envType=kubernetes) from your portainer management console
2. Ensure that portainer agent is installed in your Kubernetes cluster
3. Copy portainer agent deploy command from the above web page and run in k3sVM. The command will be as follows:
```shell
$ kubectl apply -f https://downloads.portainer.io/ce2-16/portainer-agent-k8s-lb.yaml
```
4. After successful deploy of agent, the portainer agent will be will be running and externally available via port `9001`
5. Follow instructions in management console and configure:
 `name` = `k3sVM` and `Environment address` = `<k3sVM_external_IP_address>:9001`
6. Click `Connect` button and you are all set! 