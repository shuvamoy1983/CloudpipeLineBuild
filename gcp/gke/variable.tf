variable "project" { 
  default = ""
}

variable "region" {
  default = "us-central1"
}

variable "gke_username" {
  default     = "gke"
  description = "gke username"
}

variable "gke_password" {
  default     = "Iamgoogle123456789"
  description = "gke password"
}

variable "gke_num_nodes" {
  default     = 3
  description = "number of gke nodes"
}

#variable "vpc" {
 # default = "myvpc"
#}


variable "subnetname1" {
  default = "subnet-01"
}

variable "subnetname2" {
  default = "subnet-02"
}

variable "cluster" {
  default = "eks"
}

variable "key" { 
  default = "/home/shumondal/t.json"
}

variable "azone" {
        type = list(string)
        default = ["us-central1-a", "us-central1-b"]
}


variable "vpcname" {
  type = "string"
}
