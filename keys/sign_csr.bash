#!/bin/bash
filename=$1
existing_private_ca=$2

#self sign request and create CA certificate
openssl x509 -req -days 7305 -sha1 -extensions v3_ca -signkey $existing_private_ca -in $filename.req -out $filename.cer
STATUS=$?
if [ $STATUS -eq 0 ];
then
	rm $filename.req
fi
exit $STATUS