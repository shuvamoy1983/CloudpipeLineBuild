provider "google" {
  project = var.project
  region  = var.region
  credentials = var.key
  zone    = var.zone
}

module "vpc" {
  source = "./vpc"
}

module "gke" {
  source = "./gke"
  vpcname = module.vpc.vpcname
}
