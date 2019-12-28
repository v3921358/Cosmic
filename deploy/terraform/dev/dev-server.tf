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
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm*"]
  }
}

# Setup Elastic IP
resource "aws_eip" "dietstory-dev-eip" {
  vpc = true
}

# Create Instance
resource "aws_instance" "dietstory-dev" {
  depends_on                  = [aws_eip.dietstory-dev-eip]
  ami                         = data.aws_ami.amazon-linux-2.id
  instance_type               = "t2.micro"
  associate_public_ip_address = true
  iam_instance_profile        = var.iam-ec2-role
  key_name                    = var.dev-key-pair
  vpc_security_group_ids      = [var.dietstory-sg]

  tags = {
    dietstory  = "server"
    environment = "develop"
  }

  # Install codedeploy agent & Docker
  provisioner "remote-exec" {
    connection {
      host        = self.public_ip
      user        = "ec2-user"
      private_key = file("~/.ssh/dietstory-dev-1.pem")
    }
    inline = [
      "echo 'export SERVER_HOST=${aws_eip.dietstory-dev-eip.public_ip}' >>.bashrc",
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
resource "aws_eip_association" "eip_assoc-dietstory-dev" {
  instance_id   = aws_instance.dietstory-dev.id
  allocation_id = aws_eip.dietstory-dev-eip.id
}

# Codedeploy for Game Server App
resource "aws_codedeploy_app" "dietstory-game-dev" {
  name = "dietstory-game-app-dev"
}

# Codedeploy for Game Server deployment Group
resource "aws_codedeploy_deployment_group" "dietstory-game-app-dev-group" {
  app_name              = aws_codedeploy_app.dietstory-game-dev.name
  deployment_group_name = "dietstory-game-group-dev"
  service_role_arn      = var.iam-codedeploy-role

  ec2_tag_set {
    ec2_tag_filter {
      type  = "KEY_AND_VALUE"
      key   = "environment"
      value = "develop"
    }
  }
}

# Codedeploy for Django Server App
resource "aws_codedeploy_app" "dietstory-django-dev" {
  name = "dietstory-django-app-dev"
}

# Codedeploy for Django Server deployment Group
resource "aws_codedeploy_deployment_group" "dietstory-django-app-dev-group" {
  app_name              = aws_codedeploy_app.dietstory-django-dev.name
  deployment_group_name = "dietstory-django-group-dev"
  service_role_arn      = var.iam-codedeploy-role

  ec2_tag_set {
    ec2_tag_filter {
      type  = "KEY_AND_VALUE"
      key   = "environment"
      value = "develop"
    }
  }
}
