import httplib2, sys, base64

endpoint = 'https://api.datacite.org/metadata'

if (len(sys.argv) < 4):
    raise Exception('Please provide username, password and doi')

h = httplib2.Http()
auth_string = base64.encodestring(sys.argv[1] + ':' + sys.argv[2])
response, content = h.request(endpoint + '?doi=' + sys.argv[3],
                              'DELETE',
                              headers={'Authorization':'Basic ' + auth_string})

if (response.status != 201):
    print str(response.status)
 
print(content.decode('utf-8'))
