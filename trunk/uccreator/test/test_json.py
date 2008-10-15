import os
import random
import unittest

from changefinder import JsonProcessor

from google.appengine.api import apiproxy_stub_map
from google.appengine.api import urlfetch_stub 

class TestJson(unittest.TestCase):
    
    def setUp(self):
        apiproxy_stub_map.apiproxy = apiproxy_stub_map.APIProxyStubMap()
        apiproxy_stub_map.apiproxy.RegisterStub('urlfetch', urlfetch_stub.URLFetchServiceStub()) 
        
        input = open('pipe.json')
        self.data = input.read()
        input.close()
        
        self.processor = JsonProcessor()

    def testEmpty(self):
        self.processor.process('{"items":null}', id)

    def testProcess(self):
        result = self.processor.process(self.data, 'uc.view_document')
        print self.data
        print result

if __name__ == '__main__':
    unittest.main()
