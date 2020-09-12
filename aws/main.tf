provider "aws" {
  region = "us-east-1"
}

module "vpc" {
  source = "./vpc"
}

module "eks" {
  source = "./eks"
  subnet_ids = module.vpc.app_subnet_ids
}
