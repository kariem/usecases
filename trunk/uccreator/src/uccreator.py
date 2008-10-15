#!/usr/bin/env python
"""
Use Case Creator - Change detector
$Id$
"""
import re
import os
import logging

import simplejson as json
import xml.parsers.expat as expat

from difflib import SequenceMatcher

from google.appengine.api import urlfetch
from google.appengine.ext.webapp import template, RequestHandler

rev_replace = re.compile("/r\\d+/")

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
		obj = json.loads(data).items()
		
		start = 0
		chunk = 2000
		length = len(data)
		while True:
			logging.debug(data[start:min(start+chunk, length)])
			start += chunk
			if start > length:
				break
		
		i = min(len(obj) - 1, 1)
		pipesOutput = obj[i]
		
		while type(pipesOutput) is list or type(pipesOutput) is tuple:
			if pipesOutput[0] == "items":
				break
			pipesOutput = pipesOutput[int(min(len(pipesOutput) - 1, 1))]
		
		logging.debug(pipesOutput)
		items = pipesOutput[1]
		finder = ChangeFinder(items, id)
		finder.filter()
		
		self.response.content_type = "application/json"
		json.dump(obj, self.response.out)


class ChangeFinder:
	def __init__(self, items, id):
		self.items = items
		self.xmlHandler = XmlHandler(id)
		
	def filter(self):
		emit = []
		for entry in self.items:
			if self.check(entry):
				emit.append(entry)
				
		self.items[:] = emit
	
	def check(self, entry):
		publishInfo = entry["uc"]
		rev = publishInfo["revision"]
		old_rev = int(rev) - 1
		loc = publishInfo["location"]
		old_loc = rev_replace.sub("/r" + str(old_rev) + "/", loc)
		logging.debug("Checking revision %s for %s.", rev, loc)
		
		content = self.retrieve(loc)
		if content == None:
			return False
		content_old = self.retrieve(old_loc)
		if content_old == None:
			# new file -> content has changed			
			return True

		self.xmlHandler.parse(content)
		range = self.xmlHandler.getRange()
		
		s = SequenceMatcher(None, content_old, content)
		for tag, i1, i2, j1, j2 in s.get_opcodes():
			if tag == "equal":
				continue
			if (j1 >= range[0] and j1 <= range[1]) or (j2 >= range[0] and j2 <= range[1]):
				return True
		
		return False
	def retrieve(self, url):
		result = urlfetch.fetch(url)
		status = result.status_code 
		if status != 200:
			logging.warn("Could not retrieve resource from %s. HTTP Status: %d", url, status)
			return None
		return result.content
		

class XmlHandler:
	def __init__(self, id):
		self.id = id
	def parse(self, string):
		self.start = 0
		self.end = 0
		self.depth = 0
		
		parser = expat.ParserCreate()
		parser.StartElementHandler = self.startElement
		parser.EndElementHandler = self.endElement
		self.parser = parser
		parser.Parse(string)
	def startElement(self, name, attrs):
		if self.start != 0:
			self.depth += 1
			return
		elementId = attrs.get("xml:id")
		if elementId != None:
		  if elementId == self.id:
		      self.start = self.parser.CurrentByteIndex
		      self.depth = 1
	def endElement(self, name):
		if self.start == 0 or self.end != 0:
			return
		self.depth -= 1
		if self.depth == 0:
		  self.end = self.parser.CurrentByteIndex
	def getRange(self):
		return self.start, self.end