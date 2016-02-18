import json
import os

import webapp2
from google.appengine.ext import ndb

DEFAULT_TOKEN_VALUE = 'default_token'

# Key methods


def token_key(token_value=DEFAULT_TOKEN_VALUE):
    return ndb.Key('Token', token_value)


# Helper methods


def valid_secret_key(request):
    return 'X-Secret-Key' in request.headers and request.headers["X-Secret-Key"] == os.environ.get("SECRET_KEY")


# Models


class Token(ndb.Model):
    """A GCM token to send the app users push notifications"""
    value = ndb.StringProperty(indexed=False)

# Pages


class MainPage(webapp2.RequestHandler):
    def get(self):
        if valid_secret_key(self.request):
            self.response.out.write('Valid Secret Key - nice!')
        else:
            self.response.out.write('Secret Key is not valid')


class GetTokens(webapp2.RequestHandler):
    def get(self):
        if valid_secret_key(self.request):
            token_query = Token.query()
            tokens = token_query.fetch(1000)
            response_dict = {"result": map(lambda (x): x.value, tokens)}
            self.response.write(json.dumps(response_dict, encoding='latin1'))


class AddToken(webapp2.RequestHandler):
    def post(self):
        if valid_secret_key(self.request):
            value = self.request.get('token')
            token = Token(parent=token_key(value))
            token.value = value
            token.put()
            self.response.write('Success')


class DeleteToken(webapp2.RequestHandler):
    def post(self):
        if valid_secret_key(self.request):
            value = self.request.get('token')
            token_query = Token.query(ancestor=token_key(value))
            tokens = token_query.fetch(100)
            if len(tokens) > 0:
                for token in tokens:
                    token.key.delete()
                self.response.write('Success')
            else:
                self.response.write('No token found')


app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/add_token', AddToken),
    ('/delete_token', DeleteToken),
    ('/get_tokens', GetTokens),
], debug=True)
