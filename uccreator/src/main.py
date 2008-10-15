#!/usr/bin/env python
"""
Use Case Creator - Application Entry
$Id$
"""
from uccreator import MainPage, Changes

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

application = webapp.WSGIApplication(
    [
        ('/', MainPage),
        ('/changes', Changes)
    ],
    debug=True)

def main():
  run_wsgi_app(application)

if __name__ == "__main__":
  main()