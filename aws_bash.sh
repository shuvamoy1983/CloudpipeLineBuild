cd $1
terraform init -input=false
terraform plan -out=tfplan -input=false -lock=false
terraform apply tfplan
