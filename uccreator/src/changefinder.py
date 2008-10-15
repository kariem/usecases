#!/usr/bin/env python
"""
Use Case Creator - Change detector
$Id$
"""
from difflib import SequenceMatcher

import logging
import re
import xml.parsers.expat as expat
import simplejson as json

from google.appengine.api import urlfetch

rev_replace = re.compile("/r\\d+/")

class JsonProcessor:
    def process(self, data, id):
        data = json.loads(data)
        if data.has_key("items"):
            obj = data["items"]
        elif data.has_key("value"):
            obj = data["value"]["items"]
            
        if obj == None:
            return data
        
        logging.debug(obj)
        finder = ChangeFinder(obj, id)
        finder.filter()
        return obj

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