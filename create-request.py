#!/usr/bin/python
import json
import base64

def createRequest(fname,outname):
    data = dict(filename=fname,
                __nt_name="B64FileData",
                b64_data="")
    out = base64.encodestring(open(data['filename'],'rb').read())
    data['b64_data'] = out
    open(outname,'wb').write(json.dumps(data))
    

createRequest('resources/vPrompt-Sample-EPUB2.epub',
              'resources/validate-request-with-sample-epub2.json')

createRequest('resources/vPrompt-Sample-EPUB3.epub',
              'resources/validate-request-with-sample-epub3.json')

    
# stream = open('resources/request-payload.bin','rb').read()
# data = json.loads(stream)
# open('/tmp/result.epub','wb').write(base64.decodestring(data['b64_data']))

# import pdb; pdb.set_trace()
# stream = open('resources/validate-request-with-test-pdf.json','rb').read()
# data = json.loads(stream)
# open('/tmp/result.epub','wb').write(base64.decodestring(data['b64_data']))
