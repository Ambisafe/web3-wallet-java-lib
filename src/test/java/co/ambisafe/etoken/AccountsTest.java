package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.CryptoException;
import co.ambisafe.etoken.imports.ECKey;
import co.ambisafe.etoken.model.Account;
import co.ambisafe.etoken.model.Tenant;
import co.ambisafe.etoken.service.AccountService;
import co.ambisafe.etoken.service.Keystore;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static org.junit.Assert.assertEquals;

public class AccountsTest {

    @Test
    public void containerDecryption_success() {
        String password = "Ambisafe";

        Account account = AccountService.generate(password);

        byte[] privateKey = account.getPrivateKey(password);
        ECKey derived = ECKey.fromPrivate(privateKey);

        assertEquals("Addresses must be equals", account.getAddress(), Hex.toHexString(derived.getAddress()));
        assertEquals("Public keys must be equals", account.getPublicKeyHex(), Hex.toHexString(derived.getPubKey()));
    }

    @Test(expected = CryptoException.class)
    public void containerDecryption_wrongPassword() {
        String password = "Ambisafe";
        String wrongPassword = "Ambisafe_test";

        Account account = AccountService.generate(password);
        account.getPrivateKey(wrongPassword);
    }

    @Test
    public void changeContainerPassword() {
        String oldPassword = "old";
        String newPassword = "new";

        Account account = AccountService.generate(oldPassword);
        String privateKeyWithOldPass = account.getPrivateKeyHex(oldPassword);

        account.changePassword(oldPassword, newPassword);
        String privateKeyWithNewPass = account.getPrivateKeyHex(newPassword);

        assertEquals("Private keys must be equals", privateKeyWithOldPass, privateKeyWithNewPass);
    }

    @Test
    public void generateJwtToken() throws SignatureException, NoSuchAlgorithmException, JWTVerifyException, InvalidKeyException, IOException {
        String tenantId = "baa4022d-3ac2-4774-81c7-9f5d263132fe";
        String secret = "vjuEX8xUeAtiA2qSGE+vw/9K/7Tdb1Alg7mg8fgIZ8E=";

        Tenant tenant = new Tenant(tenantId, secret);
        String token = tenant.getJwtToken("test_subject");
        System.out.println(token);

        JWTVerifier verifier = new JWTVerifier(secret);
        verifier.verify(token);
    }

    @Test
    public void saveAccountToKeystore() {
        String tenantId = "baa4022d-3ac2-4774-81c7-9f5d263132fe";
        String secret = "vjuEX8xUeAtiA2qSGE+vw/9K/7Tdb1Alg7mg8fgIZ8E=";

        Tenant tenant = new Tenant(tenantId, secret);
        String token = tenant.getJwtToken("test_subject");
        System.out.println("Token: " + token);

        String password = "Ambisafe";
        Account account = AccountService.generate(password);

        Keystore.saveAccount(token, account);
    }

    @Test
    public void saveAndGetAccountFromKeystore() {
        String tenantId = "baa4022d-3ac2-4774-81c7-9f5d263132fe";
        String secret = "vjuEX8xUeAtiA2qSGE+vw/9K/7Tdb1Alg7mg8fgIZ8E=";

        Tenant tenant = new Tenant(tenantId, secret);
        String token = tenant.getJwtToken("test_subject");
        System.out.println("Token: " + token);

        String password = "Ambisafe";
        Account account = AccountService.generate(password);

        Keystore.saveAccount(token, account);

        Account account1 = Keystore.getAccount(account.getId());
        System.out.println(account1.getPrivateKeyHex(password));
    }
}
