<h3 align="center">Spring Boot Auth Service</h3>
<p align="center">by using JWT auth tokens</p>

## Routes

| Route | Method | Request Body | Response |
|-------|--------|--------------|------------|
| /register | POST | <code>{ "fullName": "Test User", "username": "test", "emailAddress": "some@email.com", "password": "supersafepassword" }</code> | <code>{ "username": "test", "email": "some@email.com", "created": true }</code> | 
| /login | POST | <code>{ "username": "test", "password": "supersafepassword" }</code> | Bearer \<JWT Token\> |

---
JWT Token required in `Authorization` Header:

| Route | Method | Request Body | Response |
|-------|--------|--------------|------------|
| /user | GET    | -            | <code>{ "username": "test", "fullName": "Test User", "emailAddress": "some@email.com" }</code> |
| /user/changepassword | POST | { "oldPassword": "supersafepassword", "newPassword": "supersafepassword2" } | { "success": "true" } |
