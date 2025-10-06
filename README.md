# How to use
## Register Author API
```bash
curl -X POST \
     -H "Content-Type: application/json" \
     -d '{"id":0,"name":"Osamu Dazai","birthDate":"1930-05-11"}' \
     http://localhost:8080/author
```
## Update Author API
```bash
curl -X PUT \
     -H "Content-Type: application/json" \
     -d '{"id":1,"name":"Osamu Dazai","birthDate":"1930-05-11"}' \
     http://localhost:8080/author
```
## Get Book API
```bash
$ curl "http://localhost:8080/book?authorId=1"
```
## Register Book API
```bash
curl -X POST \
     -H "Content-Type: application/json" \
     -d '{"id":0,"title":"Sample Book","price":2500,"isPublished":true,"authorIdList":[1,3]}' \
     http://localhost:8080/book
```
## Update Book API
```bash
curl -X PUT \
     -H "Content-Type: application/json" \
     -d '{"id":1,"title":"Sample Book","price":2500,"isPublished":true,"authorIdList":[1,3]}' \
     http://localhost:8080/book
```
