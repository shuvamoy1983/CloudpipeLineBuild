

module "network" {
  source  = "terraform-google-modules/network/google"

  network_name = var.vpc_name
  project_id   = var.project

  subnets = [
    {
      subnet_name   = var.subnetname1
      subnet_ip     = var.subnet1
      subnet_region = var.region

    },
 
   {
     subnet_name   = var.subnetname2
      subnet_ip     = var.subnet2
      subnet_region = var.region
   }
   
  ]

 
}

module "network_routes" {
  source  = "terraform-google-modules/network/google//modules/routes"
  network_name = module.network.network_name
  project_id   = var.project

   routes = [
         {
             name                   = "egress-internet"
             description            = "route through IGW to access internet"
             destination_range      = "0.0.0.0/0"
             tags                   = "egress-inet"
             next_hop_internet      = "true"
         },

     ]
  }

// VPC firewall configuration
resource "google_compute_firewall" "firewall" {
  name    = "${module.network.network_name}-firewall"
  network = module.network.network_name

  allow {
    protocol = "icmp"
  }

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["0.0.0.0/0"]
}
