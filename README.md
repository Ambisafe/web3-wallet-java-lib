# web3-wallet-java-lib

## Usage
>**NOTICE**: all methods throws only RuntimeExceptions for convenience, 
so be carefuly and check JavaDoc method signature to handle exceptions

### Accounts
#### Create
Account generating and encryption with user **password**:
```java
String password = "Ambisafe_Pass";
Account account = Account.generate(password);
```
This will generate new random ECKey pair and encrypt user container with password.  
Generated Account will have this structure:
```js
{  
  "address":"3f8a06ccfa1eca91f63c531fc4027f4740189819",
  "container":{  
    "data":"eb6c804bd6693b8c8b342869ba4fef236f068fa0db433290fc37fa23b441415786eeb1172c5d6171c1553ebf36215909",
    "iv":"bdde14eb178775ebb202849823caacb7",
    "public_key":"04f86cfd239b4ed348eea7b12951f07f0a8bbf07386e29b001e5bbceb85544f5ec3c35f95456036baf1f81adc3dd7385d61af2d3c2b672ce4b03353abde1f2b675",
    "salt":"6deca655-5109-4c13-bd03-a79d9c90824d"
  },
  "id":"3a6c1c9d-26de-4347-ba23-88538f6de4c4",
  "version":"0"
}
```
Where:
  - **address** is the user's ethereum address;
  - **container** has encrypted private key and open public key;
  - unique **id** for Keystore;
  - **version** will be zero (newly generated Accounts has zero version).
  
#### Decrypt
For Account decryption you need to specify user password:
```java
String privateKeyHex = account.getPrivateKeyHex(password);
// a54196aa6e95642c41f700c5a42841e41150f242a680fceffa76bdc90265a72e
```
>**NOTICE**: if wrong password was specified, CryptoException will be thrown.

#### Change password
This will change user password and reencrypt existing container with new one:
```java
String oldPassword = "Simple_Pass123";
String newPassword = "More_Secure_Password";
account.changePassword(oldPassword, newPassword);
```
>**NOTICE**: if wrong password was specified, CryptoException will be thrown.

This action reecrypts container with new password, generates new unique **id** and increment **version** by one.

## Ambisafe services
### Tenant and JWT tokens
To deal with Ambisafe services you need to generate JWT token with right subject for request authorization.
To generate new one you need to specify your tenant **key**, **secret** and target service **subject**:
```java
String key = "dcc4732d-3ac2-81c7-4074-3132fe9f5d26";
String secret = "K8gTgvZ+eqIiAbE1l8x/7GXAwE/f792utdAjg8vSUmE=";
String subject = "some_subject";

Tenant tenant = new Tenant(key, secret);
String jwtToken = tenant.getJwtToken(subject);
// eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJkY2M0NzMyZC0zYWMyLTgxYzctNDA3NC0zMTMyZmU5ZjVkMjYiLCJzdWIiOiJzb21lX3N1YmplY3QiLCJhdWQiOiJhbWJpc2FmZSIsImV4cCI6MTQ3ODc5MTIxOSwianRpIjoiMDg5ZjEyN2YtNjI4Yy00NjhkLTlkNGYtNjI5N2RjNTNhMmJhIn0.tHEd6qWi-jJTIGKoPsHz7Olv8wvGwKupxoqgHVywOR8
```
Decoded jwt token looks like this:
```js
{
  "iss": "dcc4732d-3ac2-81c7-4074-3132fe9f5d26",
  "sub": "some_subject",
  "aud": "ambisafe",
  "exp": 1478791219,
  "jti": "089f127f-628c-468d-9d4f-6297dc53a2ba"
}
```
This method generates new jwt token and sign it by your tenant secret key to authorize request on service side.

### Keystore
This will presist account in Ambisafe Keystore service:
```java
// generate account
String password = "Ambisafe_Pass";
Account account = Account.generate(password);

// generate jwt token
String key = "dcc4732d-3ac2-81c7-4074-3132fe9f5d26";
String secret = "K8gTgvZ+eqIiAbE1l8x/7GXAwE/f792utdAjg8vSUmE=";
String subject = "some_subject";

Tenant tenant = new Tenant(key, secret);
String jwtToken = tenant.getJwtToken(subject);

// save account
Keystore.saveAccount(jwtToken, account);
```

To retrieve account from Ambisafe Keystore service you need to specify **account id**:
```java
// generate account
String password = "Ambisafe_Pass";
Account account = Account.generate(password);

// generate jwt token
String key = "dcc4732d-3ac2-81c7-4074-3132fe9f5d26";
String secret = "K8gTgvZ+eqIiAbE1l8x/7GXAwE/f792utdAjg8vSUmE=";
String subject = "some_subject";

Tenant tenant = new Tenant(key, secret);
String jwtToken = tenant.getJwtToken(subject);

// save account
Keystore.saveAccount(jwtToken, account);

// retrieve account
Account accFromKeystore = Keystore.getAccount(account.getId());
String privateKeyHex = accFromKeystore.getPrivateKeyHex(password);
```
