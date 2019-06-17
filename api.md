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

fail code:

0: Server Error
1: Username used

- ## `http://url:port/login` POST
Posts the id and password or token to server. You can login with token.

1. `{'id':'test','pw':'test'}`
1. `{'id':'test','token':'TOKEN_FOR_TEST'}`

Then response would be like this.

1. `{'success':true,'token':'TOKEN_REGENERATED'}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

fail code:

0: Server Error
1: Username not found
2: Password/Token incorrect

- ## `http://url:port/logout` POST

Posts the id and token to server. You can login with token.

1. `{'id':'test','token':'TOKEN_FOR_TEST'}`

Then response would be like this.

1. `{'success':true}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

fail code:

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

fail code:

0: Server Error
1: Username not found
2: Password incorrect

- ## `http://url:port/profile-image` GET

Gets the image of profile image.

1. `?id=ID_TO_GET`
(id should be encoded)

Then response would be like this.

1. `{'success':true,'img':'IMG_DATA'}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

fail code:

0: Server Error
1: Username not found

- ## `http://url:port/profile-image` POST

Posts the image of profile image.

1. `{'id':'test','token':TOKEN_FOR_TEST,'img':BASE_64_ENCODED_IMAGE}`

Then response would be like this.

1. `{'success':true,'token':RENEWED_TOKEN}`
when login was a success
1. `{'success':false,'code':ERR_CODE}`
when login was failed

fail code:

0: Server Error
1: Username not found
2: Token is not incorrect