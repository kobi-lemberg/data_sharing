#!/usr/bin/env bash
sudo docker build /home/ubuntu -t kobilemberg/valstorage
sudo docker run -t  -p 8080:8080 kobilemberg/valstorage