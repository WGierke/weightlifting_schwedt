#!/usr/bin/python
# -*- coding: iso-8859-15 -*-
from gcm import GCM
import requests
import json
import ConfigParser
import os
import sys


def send_appspot_get_request(url, secret_key):
    return requests.get("http://weightliftingschwedt.appspot.com/" + url, headers={"Content-Type": "application/json", "X-Secret-Key": secret_key}).content


def send_appspot_delete_request(url, secret_key, data):
    return requests.post("http://weightliftingschwedt.appspot.com/" + url, data=data, headers={"Content-Type": "application/x-www-form-urlencoded", "X-Secret-Key": secret_key}).content


config = ConfigParser.RawConfigParser(allow_no_value=True)
config.read('server/config.ini')
gcm_key = config.get("gcm", "API-Key")
secret_key = config.get("appspot", "X-Secret-Key")
push_messages_file = "server/push_messages.txt"

if os.path.isfile(push_messages_file):
    push_messages = open(push_messages_file,'r').read().split('\n')
else:
    print "There is no file containing push messages that should be delivered."
    sys.exit()

appspot_response = send_appspot_get_request("get_tokens", secret_key)
appspot_tokens = json.loads(appspot_response)["result"]

gcm = GCM(gcm_key)

push_messages = [line for line in push_messages if line != '']
print "Push Messages: " + '\n'.join(push_messages)

#Example Message: 'New Article#Victory in Görlitz#Push the button ...#4'
for line in push_messages:
    data = {'update': line.decode('utf-8')}
    sent_requests = 0
    receivers = []
    for appspot_token in appspot_tokens:
        if appspot_token not in receivers:
            gcm_push_response = gcm.json_request(registration_ids=[appspot_token], data=data)
            if bool(gcm_push_response):
                print appspot_token + " is invalid. Sending request to remove it."
                send_appspot_delete_request("delete_token", secret_key, "token=" + appspot_token)
            else:
                print "Sent " + line.decode('utf-8') + " to " + appspot_token
                receivers.append(appspot_token)
                sent_requests += 1


print "Sent to " + str(sent_requests) + " receivers"

os.remove("server/push_messages.txt")
