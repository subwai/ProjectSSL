#!/bin/bash
filename=$1

#generate CA private key
openssl genrsa -des3 -out $filename.key 2048
chmod 600 $filename.key

#generate CA self sign request
openssl req -new -key $filename.key -out $filename.req

#self sign request and create CA certificate
openssl x509 -req -days 7305 -sha1 -extensions v3_ca -signkey $filename.key -in $filename.req -out $filename.crt

rm $filename.req