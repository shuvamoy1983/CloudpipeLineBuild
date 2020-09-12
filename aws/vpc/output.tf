output "vpc_id" {
  value = "${aws_vpc.terra_vpc.id}"
}

output "app_subnet_ids" {
  value = "${aws_subnet.public.*.id}"
}

