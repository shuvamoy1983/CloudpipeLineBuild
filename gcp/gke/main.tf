

resource "google_container_cluster" "primary" {
  name     = var.cluster
  location = var.region
  network    = var.vpcname
  subnetwork = var.subnetname1
  vertical_pod_autoscaling {
  enabled = "true"
  }
  initial_node_count = 1
  remove_default_node_pool = true

 }


resource "google_container_node_pool" "primary_preemptible_nodes" {
  name       = "my-test-pool"
  location   = var.region
  cluster    = google_container_cluster.primary.name
  initial_node_count = 2


   autoscaling {
    min_node_count = "2"
    max_node_count = "5"
  }


  node_locations  = var.azone

  management {
    auto_repair  = "false"
    auto_upgrade = "false"
  }


  node_config {
    preemptible  = true
    machine_type = "e2-standard-8"

     labels = {
      all-pools-example = "true"
    }

    metadata = {
      disable-legacy-endpoints = "true"
    }

    oauth_scopes = [
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
    ]
  }
}
