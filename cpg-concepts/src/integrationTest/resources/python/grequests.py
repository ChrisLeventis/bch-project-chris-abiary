import grequests

url = "https://example.com/api"
payload = {"name": "Frank", "age": 50}

req = grequests.post(url, json=payload)
response = grequests.map([req])[0]

print(response.status_code)
print(response.json())
