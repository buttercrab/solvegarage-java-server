# API document for client

- ## `http://url:port/get-key` GET
Gets the server's RSA public key. It won't change before restarting the server

- ## `http://url:port/register` POST
Posts the id and password to server.

1. `{'id':'test','pw':'test'}`

Then response would be like this.

1. `{'success':true,'token':'TOKEN_REGENERATED'}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

login fail code:

0: Server Error
1: Username used

- ## `http://url:port/login` POST
Posts the id and password or token to server. You can login with token.

1. `{'id':'test','pw':'test'}`
1. `{'id':'test','tk':'TOKEN_FOR_TEST'}`

Then response would be like this.

1. `{'success':true,'token':'TOKEN_REGENERATED'}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

login fail code:

0: Server Error
1: Username not found
2: Password/Token incorrect

- ## `http://url:port/logout` POST

Posts the id and token to server. You can login with token.

1. `{'id':'test','tk':'TOKEN_FOR_TEST'}`

Then response would be like this.

1. `{'success':true}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

login fail code:

0: Server Error
1: Username not found
2: Token incorrect or logged out already

- ## `http://url:port/delete-account` POST

Posts the id and password to server. You can login with token.

1. `{'id':'test','pw':'test'}`

Then response would be like this.

1. `{'success':true}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

login fail code:

0: Server Error
1: Username not found
2: Password incorrect