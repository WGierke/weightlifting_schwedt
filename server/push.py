from gcm import GCM
import requests
import json
import ConfigParser

def send_parse_api_request(method, url, app_id, rest_key):
    request_method = getattr(requests, method)
    response = request_method(url, headers={"Content-Type": "application/json", "X-Parse-Application-Id": app_id, "X-Parse-REST-API-Key": rest_key}).content
    return response

config = ConfigParser.RawConfigParser(allow_no_value=True)
config.read('config.ini')
application_id = config.get("parse", "X-Parse-Application-Id")
rest_key = config.get("parse", "X-Parse-REST-API-Key")
gcm_key = config.get("gcm", "API-Key")

registration_ids_response = send_parse_api_request("get", "https://api.parse.com/1/classes/GcmToken", application_id, rest_key)

gcm_token_objects = json.loads(registration_ids_response)["results"]

gcm = GCM(gcm_key)
data = {'update': 'value2'}

for obj in gcm_token_objects:
    gcm_push_response = gcm.json_request(registration_ids=[obj["token"]], data=data)
    if bool(gcm_push_response):
        print obj["token"] + " is invalid. Sending request to remove it."
        send_parse_api_request("delete", "https://api.parse.com/1/classes/GcmToken/" + obj["objectId"], application_id, rest_key)