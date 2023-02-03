---
marp: true
title: Terraform Training
paginate: true
theme: uncover
style: |
    h1 {
    text-align: left;
    }
    p {
        text-align: left
    }
footer: "HSP Training"


---
![bg](./images/philips.jpeg)
<!-- 
_color: white 
-->
# Terraform: From Zero to Hero

Shinoj Prabhakaran

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# Pre-requisites
->Install multipass
->Create VMs in multipass for docker and k3s
->Install Terraform

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# What will be covered?
->Terraform Basics
->Getting Started With:
-->1.Local Deployment
-->2.HSP Classic Deployment
-->3.Local K3S Deployment
-->4.HSP EKS Deployment

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# Terraform
![w:900 h:500](./images/tf1.png)

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# Basics Demo
In this section, you will learn:
->How to write IaC?
->Using Provider
->Plan & Apply
->Update Plan
->Destroy

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# HSP CloudFoundry Demo
In this section, you will learn:
->Deploy to CloudFoundry 
->With service broker dependency
-->By creating a HSP provided service   
->Structure your terraform scripts
-->Terraform Modules
->Understanding Terraform State
->And other useful commands...

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# HSP CloudFoundry Demo
![w:900 h:500](./images/cf-deploy.png)

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# HSP CloudFoundry Demo
![w:900 h:250](./images/cf-flow.png)

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# Kubernetes Demo
In this section, you will learn:
->How to deploy an application in Kubernetes cluster
->Applicable for HSP AWS services 

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# Kubernetes Demo
![w:900 h:500](./images/cluster_demo.png)

---
![bg](./images/bg.webp)
<!-- _class: lead-->
# HSP AWS Demo
![w:900 h:500](./images/eks_demo.png)