#!/usr/bin/python
# -*- coding: iso-8859-15 -*-
from gcm import GCM
import requests
import json
import ConfigParser
import os
import sys

def send_parse_api_request(method, url, app_id, rest_key):
    request_method = getattr(requests, method)
    response = request_method(url, headers={"Content-Type": "application/json", "X-Parse-Application-Id": app_id, "X-Parse-REST-API-Key": rest_key}).content
    return response

config = ConfigParser.RawConfigParser(allow_no_value=True)
config.read('server/config.ini')
application_id = config.get("parse", "X-Parse-Application-Id")
rest_key = config.get("parse", "X-Parse-REST-API-Key")
gcm_key = config.get("gcm", "API-Key")
push_messages_file = "server/push_messages.txt"

registration_ids_response = send_parse_api_request("get", "https://api.parse.com/1/classes/GcmToken", application_id, rest_key)
gcm_token_objects = json.loads(registration_ids_response)["results"]

gcm = GCM(gcm_key)

if os.path.isfile(push_messages_file):
    push_messages = open(push_messages_file,'r').read().split('\n')
else:
    print "There is no file containing push messages that should be delivered."
    sys.exit()

push_messages = [line for line in push_messages if line != '']

receivers = []

#Example Message: 'New Article#Victory in Görlitz#Push the button ...#4'
for line in push_messages:
    data = {'update': line.decode('utf-8')}
    sent_requests = 0
    for obj in gcm_token_objects:
        reg_id = obj["token"]
        if not reg_id in receivers:
            gcm_push_response = gcm.json_request(registration_ids=[reg_id], data=data)
            if bool(gcm_push_response):
                print reg_id + " is invalid. Sending request to remove it."
                send_parse_api_request("delete", "https://api.parse.com/1/classes/GcmToken/" + obj["objectId"], application_id, rest_key)
            else:
                print "Sent " + line.decode('utf-8') + " to " + reg_id
                receivers.append(reg_id)
                sent_requests += 1
        else:
            print reg_id + " is already saved. Sending request to remove it."
            print send_parse_api_request("delete", "https://api.parse.com/1/classes/GcmToken/" + obj["objectId"], application_id, rest_key)

print "Sent to " + str(sent_requests) + " receivers"

os.remove("server/push_messages.txt")