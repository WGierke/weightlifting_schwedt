import cgi
import urllib

from google.appengine.api import users
from google.appengine.ext import ndb
from google.appengine.ext import db

import webapp2

DEFAULT_TOKEN_VALUE = 'default_token'


def token_key(token_value=DEFAULT_TOKEN_VALUE):
    return ndb.Key('Token', token_value)


class Token(ndb.Model):
    """A GCM token to send the app users push notifications"""
    value=ndb.StringProperty(indexed=False)



class MainPage(webapp2.RequestHandler):
    def get(self):
        token_query = Token.query(ancestor=token_key(DEFAULT_TOKEN_VALUE))
        tokens = token_query.fetch(100)
        self.response.write(str(len(tokens)))
        self.response.out.write('''
        <html>
          <body>
            <form method="post" action="/add_token">
              <p>Name: <input type="text" name="token" /></p>
              <p><input type="submit" /></p>
            </form>
          </body>
        </html>
        ''')

        for token in tokens:
            self.response.write('<b>%s</b> wrote:' % token)


class AddToken(webapp2.RequestHandler):
    def post(self):
        self.response.write('end')
        value = self.request.get('token')
        self.response.write(value)
        token = Token(parent=token_key(DEFAULT_TOKEN_VALUE))
        token.value = value
        token.put()


class DeleteToken(webapp2.RequestHandler):
    def delete(self):
        value = self.request.get('token')
        self.response.write(value)
        db.delete(db.Key.from_path('Token', value))

app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/add_token', AddToken),
    ('/delete_token', DeleteToken),
], debug=True)