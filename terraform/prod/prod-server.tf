# Variables
variable "region" {
  default = "ca-central-1"
}
variable "iam-profile" {
  default = "Dietstory-EC2-Role"
}
variable "dev-key-pair" {
  default = "dietstory-dev-1"
}
variable "dietstory-sg" {
  default = "sg-0014d9e1edca2057f"
}

# Use AWS Profile's Provider
provider "aws" {
  profile = "default"
  region  = var.region
}

# Find Amazon Linux 2 AMI
data "aws_ami" "amazon-linux-2" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm*"]
  }
}

# Create Instance
resource "aws_instance" "dietstory-prod" {

  ami                         = data.aws_ami.amazon-linux-2.id
  instance_type               = "t3.small"
  associate_public_ip_address = true
  iam_instance_profile        = var.iam-profile
  key_name                    = var.dev-key-pair
  vpc_security_group_ids      = [var.dietstory-sg]

  # Install codedeploy agent
  provisioner "remote-exec" {
    connection {
      host = self.public_ip
      user = "ec2-user"
      private_key = file("~/.ssh/dietstory-prod-1.pem")
    }
    inline = [
      "sudo yum -y update",
      "sudo yum -y install ruby",
      "cd /tmp",
      "wget https://aws-codedeploy-${var.region}.s3.amazonaws.com/latest/install",
      "chmod +x ./install",
      "sudo ./install auto",
      "sudo service codedeploy-agent start",
    ]
  }
}