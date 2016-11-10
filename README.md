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
