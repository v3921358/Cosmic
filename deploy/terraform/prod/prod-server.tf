# Variables
variable "region" {
  default = "ca-central-1"
}
variable "iam-ec2-role" {
  default = "Dietstory-EC2-Role"
}
variable "iam-codedeploy-role" {
  default = "arn:aws:iam::308473842533:role/Dietstory-CodeDeploy-Role"
}
variable "prod-key-pair" {
  default = "dietstory-prod-1"
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
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm*"]
  }
}

# Setup Elastic IP
resource "aws_eip" "dietstory-prod-eip" {
  vpc = true
}

# Create Instance
resource "aws_instance" "dietstory-prod" {
  depends_on                  = [aws_eip.dietstory-prod-eip]
  ami                         = data.aws_ami.amazon-linux-2.id
  instance_type               = "t3.small"
  associate_public_ip_address = true
  iam_instance_profile        = var.iam-ec2-role
  key_name                    = var.prod-key-pair
  vpc_security_group_ids      = [var.dietstory-sg]

  tags = {
    dietstory  = "server"
    environment = "production"
  }

  # Install codedeploy agent & Docker
  provisioner "remote-exec" {
    connection {
      host        = self.public_ip
      user        = "ec2-user"
      private_key = file("~/.ssh/dietstory-prod-1.pem")
    }
    inline = [
      "echo 'export SERVER_HOST=${aws_eip.dietstory-prod-eip.public_ip}' >>.bashrc",
      "source ~/.bashrc",
      "sudo yum -y update",
      "sudo yum -y install docker",
      "sudo yum -y install ruby",
      "cd /tmp",
      "wget https://aws-codedeploy-${var.region}.s3.amazonaws.com/latest/install",
      "chmod +x ./install",
      "sudo ./install auto",
      "sudo gpasswd -a $USER docker",
      "sudo service codedeploy-agent start",
      "sudo service docker start",
      "sudo systemctl enable docker",
    ]
  }
}

# Associate Elastic IP with EC2
resource "aws_eip_association" "eip_assoc-dietstory-prod" {
  instance_id   = aws_instance.dietstory-prod.id
  allocation_id = aws_eip.dietstory-prod-eip.id
}

# Codedeploy for Game Server App
resource "aws_codedeploy_app" "dietstory-game-prod" {
  name = "dietstory-game-app-prod"
}

# Codedeploy for Game Server deployment Group
resource "aws_codedeploy_deployment_group" "dietstory-game-app-prod-group" {
  app_name              = aws_codedeploy_app.dietstory-game-prod.name
  deployment_group_name = "dietstory-game-group-prod"
  service_role_arn      = var.iam-codedeploy-role

  ec2_tag_set {
    ec2_tag_filter {
      type  = "KEY_AND_VALUE"
      key   = "environment"
      value = "production"
    }
  }
}

# Codedeploy for Django Server App
resource "aws_codedeploy_app" "dietstory-django-prod" {
  name = "dietstory-django-app-prod"
}

# Codedeploy for Django Server deployment Group
resource "aws_codedeploy_deployment_group" "dietstory-django-app-prod-group" {
  app_name              = aws_codedeploy_app.dietstory-django-prod.name
  deployment_group_name = "dietstory-django-group-prod"
  service_role_arn      = var.iam-codedeploy-role

  ec2_tag_set {
    ec2_tag_filter {
      type  = "KEY_AND_VALUE"
      key   = "environment"
      value = "production"
    }
  }
}
