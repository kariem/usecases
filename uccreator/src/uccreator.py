#!/usr/bin/env python
"""
Use Case Creator - Request handlers
$Id$
"""
from changefinder import JsonProcessor

import os
import logging

import simplejson as json

from google.appengine.ext.webapp import template, RequestHandler

class MainPage(RequestHandler):
	def get(self):
		path = os.path.join(os.path.dirname(__file__), 'index.html')
		self.response.out.write(template.render(path, {}))

class Changes(RequestHandler):
	def get(self):
		self.response.headers['Content-Type'] = 'text/plain'
		self.response.out.write('Revision Information')
		
	def post(self):
		data = self.request.get("data")
		id = self.request.get("uc_id")
		logging.info("Called with uc_id: %s, data: %d chars", id, len(data))
		self.debugData(data)
		
		processor = JsonProcessor()
		result = processor.process(data, id)
		self.response.content_type = "application/json"
		json.dump(result, self.response.out)
		
	def debugData(self, data):
		start = 0
		chunk = 2000
		length = len(data)
		while True:
			logging.debug(data[start:min(start+chunk, length)])
			start += chunk
			if start > length:
				break
