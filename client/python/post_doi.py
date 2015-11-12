import requests, sys, codecs
 
#endpoint = 'https://mds.datacite.org/doi'
endpoint = 'https://mds.test.datacite.org/doi'

if (len(sys.argv) < 4):
    raise Exception('Please provide username, password, location of doi-url file')
 
username, password, filename = sys.argv[1:]

file = codecs.open(sys.argv[3], 'r', encoding='utf-8').read().strip()

response = requests.post(endpoint,
                         auth = (username, password),
                         data = file.encode('utf-8'),
                         headers = {'Content-Type':'text/plain;charset=UTF-8'})

print str(response.status_code) + " " + response.text