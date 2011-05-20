import httplib2, sys, base64

endpoint = 'https://mds.datacite.org/metadata'

if (len(sys.argv) < 4):
    raise Exception('Please provide username, password and doi')

h = httplib2.Http()
auth_string = base64.encodestring(sys.argv[1] + ':' + sys.argv[2])
response, content = h.request(endpoint + '/' + sys.argv[3],
                              headers={'Accept':'application/xml',
                                       'Authorization':'Basic ' + auth_string})

if (response.status != 200):
    print str(response.status)
 
print(content.decode('utf-8'))
