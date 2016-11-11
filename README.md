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

### Keystore (http://keystore-docs.rtfd.io/)
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

### EToken
To get your balance specify **address** and asset **symbol**:
```java
String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
String symbol = "CC";
BigInteger balance = AmbisafeNode.EToken.getBalance(address, symbol);
// 705000
```
 To get transactions count for **address**:
```java
String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
BigInteger txCount = AmbisafeNode.getTransactionsCount(address);
// 5
```

To sent transaction specify **recipient**, **amount** to send, asset **symbol** and private key from account to sign transaction:
```java
String recipient = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
String amount = "0.005";
String symbol = "SP";
byte[] privateKey = account.getPrivateKey(password);

String txHash = AmbisafeNode.EToken.transfer(recipient, amount, symbol, privateKey);
// 0x157e70f18e7a6d4f61dd5704a4180adc6f0395ab2a3b31e0e9b34573d1b366d2
```
>**NOTICE**: throws RestClientException/

This action returns hash of the transaction.

### ETokenETH
This things also are available for ETokenETH the same way, just change class to **ETokenETH**.
```java
String address = "0x182c44e3afd39811947d344082ec5fd9e6c0a6b7";
BigInteger balance = AmbisafeNode.ETokenETH.getBalance(address);
// 100500
```

Account activation:
```java
byte[] privateKey = account.getPrivateKey(password);
AmbisafeNode.ETokenETH.activateAccount(privateKey);
```

### EToken History (http://etoken-history-docs.rtfd.io/)
To get transactions list by **recipient**:
```java
String recipient = "0x60dda47483288e673dc0d522a715ae8eed5d60fd";
ETokenHistory.TxList txList = ETokenHistory.getTxList(recipient);
```
You also can specify additional parameters like key value pair to query some portion of data (see more at http://etoken-history-docs.rtfd.io/#parameters):
```java
String recipient = "0x60dda47483288e673dc0d522a715ae8eed5d60fd";
ETokenHistory.TxList txList = ETokenHistory.getTxList(recipient, "max", "2", "skip", "1");
```

TxList has three field:
  - **txList** - list of Tx (transactions) objects;
  - **total** - total transactions in this page;
  - **nextRequest** - generated url link for next portion with the same query parameters.

Tx has this structure with all info about transaction:
```js
{  
  "txHash":"0xa13db547691ff1db0e53cef5dcd3b3bdee4d9e04856db65972bcd174ae7d3d4d",
  "timestamp":"1471671193",
  "blockNumber":"2104970",
  "confirmations":"1",
  "eventName":"TransferToICAP",
  "from":"0x94afcdba23744dfcfdf007f51550c9881de87038",
  "reference":"",
  "value":"1",
  "to":"0x60dda47483288e673dc0d522a715ae8eed5d60fd",
  "icap":"XE60KUNKUNASQG47ISFQ",
  "symbol":"null"
}
```
To get next portion of data just invoke **TxList.requestNextPage** method:
```java
String recipient = "0x60dda47483288e673dc0d522a715ae8eed5d60fd";
ETokenHistory.TxList txList = ETokenHistory.getTxList(recipient, "max", "2", "skip", "1");

if (txList.hasNextPage()) {
    txList.requestNextPage();
    // do something with new portion of data
}
```
