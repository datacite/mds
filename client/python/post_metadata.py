import requests, sys, codecs
 
#endpoint = 'https://mds.datacite.org/metadata'
endpoint = 'https://mds.test.datacite.org/metadata'

if (len(sys.argv) < 4):
    raise Exception('Please provide username, password and location of metadata file')

username, password, filename = sys.argv[1:]

metadata = codecs.open(filename, 'r', encoding='utf-8').read()

response = requests.post(endpoint,
                         auth = (username, password),
                         data = metadata.encode('utf-8'),
                         headers = {'Content-Type':'application/xml;charset=UTF-8'})

print str(response.status_code) + " " + response.text
