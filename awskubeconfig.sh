cd $1
rm -rf  $HOME/.kube/*
cp terraform.tfstate $HOME/.kube/
mv $HOME/.kube/terraform.tfstate $HOME/.kube/config-eks-tf
export KUBECONFIG=${HOME}/.kube/config-eks-tf:${HOME}/.kube/config
echo "export KUBECONFIG=${KUBECONFIG}" >> ${HOME}/.bash_profile
aws eks --region us-east-1 update-kubeconfig --name eks
