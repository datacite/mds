import httplib2, sys, base64, codecs
 
if (len(sys.argv) < 4):
    raise Exception('Please provide username, password, location of doi-url file')
 
endpoint = 'https://mds.datacite.org/doi'

body_unicode = codecs.open(sys.argv[3], 'r', encoding='utf-8').read().strip()

print(body_unicode);

h = httplib2.Http()
auth_string = base64.encodestring(sys.argv[1] + ':' + sys.argv[2])

response, content = h.request(endpoint,
                              'PUT',
                              body = body_unicode.encode('utf-8'),
                              headers={'Content-Type':'text/plain;charset=UTF-8',
                                       'Authorization':'Basic ' + auth_string})
if (response.status != 201):
    print str(response.status)
 
print(content.decode('utf-8'))
