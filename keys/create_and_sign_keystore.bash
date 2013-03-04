#!/bin/bash
keyname=$1
existing_ca=$2

echo "############ generate key"
keytool -genkey -keyalg RSA -keysize 2048 -keystore $keyname.jks -alias $keyname -dname "CN=$keyname, O=LTH, L=Lund, S=Skane, C=SE"

echo "############ certificate request"
keytool -certreq -alias $keyname -keyalg RSA -file $keyname.req -keystore $keyname.jks

echo "############ sign request"
openssl x509 -req -CA $existing_ca.crt -CAkey $existing_ca.key -in $keyname.req -out $keyname.cer -days 7305 -CAcreateserial

echo "############ import root"
keytool -import -alias root -keystore $keyname.jks -file $existing_ca.crt

echo "############ import signed certificate"
keytool -import -alias $keyname -keystore $keyname.jks -file $keyname.cer

rm $keyname.cer
rm $keyname.req
rm $existing_ca.srl