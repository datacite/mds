import httplib2, sys, base64, codecs
 
if (len(sys.argv) < 4):
    raise Exception('Please provide username, password and location of metadata file')
 
endpoint = 'https://api.datacite.org/metadata'

body_unicode = codecs.open(sys.argv[3], 'r', encoding='utf-8').read()

print(body_unicode);

h = httplib2.Http()
auth_string = base64.encodestring(sys.argv[1] + ':' + sys.argv[2])
response, content = h.request(endpoint,
                              'POST',
                              body = body_unicode.encode('utf-8'),
                              headers={'Content-Type':'application/xml;charset=UTF-8',
                                       'Authorization':'Basic ' + auth_string})
if (response.status != 201):
    print str(response.status)
 
print(content.decode('utf-8'))
