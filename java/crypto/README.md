# Cryptography in Java

## Encryption and Decryption done the right way.

* External keystore file is used to keep secret keys.
* There can be multiple secret keys with different key types.
* Key alias need to be prefixed with key type prefix.
* By default encryption will use latest key to encrypt.
* Decryption can take key alias as input to decrypt value. as it is possible that encrypted value was encrypted with older key.
* Encryption logic appends obfuscate bytes at start to make encrypted value unique even for same inputs. This obfuscate bytes are removed automatically while decrypting.

* Generate key with following command
  * `keytool -genseckey -keystore keystore.p12 -storetype pkcs12 -storepass password -keyalg AES -keysize 256 -alias first_key`


* Best practice
  * Generate key with prefix of given KeyType and append date format yyyyMMddHHmm.
  * This will ensure last generated key is treated as latest and used by default while encrypting.
  * while storing in db. use encoded value of SafeTuple to keep key alias along with encrypted value. This is to ensure even if new key is added in keystore older encrypted db value can be decrypted usnig old keys.
  * Example to add key `keytool -genseckey -keystore keystore.p12 -storetype pkcs12 -storepass password -keyalg AES -keysize 256 -alias default.202208151920`