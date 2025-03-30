import httpx

url = "https://jsonplaceholder.typicode.com/posts"
payload = {"title": "Hello", "body": "This is a test post", "userId": 1}

response = httpx.post(url, json=payload)


