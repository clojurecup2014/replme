# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

shell = <<BASH
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y docker.io openjdk-7-jre
echo "DOCKER_OPTS=\"-H tcp://localhost:8181 -H unix:///var/run/docker.sock\"" > /etc/default/docker.io
sudo service docker.io restart
wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/local/bin/lein
chmod a+x /usr/local/bin/lein
docker.io pull edpaget/lein
BASH

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "phusion/ubuntu-14.04-amd64"
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.provision "shell", inline: shell
  config.vm.provider "virtualbox" do |v|
    v.memory = 1024
  end
end
