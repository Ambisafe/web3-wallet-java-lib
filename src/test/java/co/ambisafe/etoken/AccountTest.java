package co.ambisafe.etoken;

import co.ambisafe.etoken.exception.CryptoException;
import org.junit.Test;

public class AccountTest {

    @Test
    public void generateNewAccount() {
        String password = "Ambisafe";

        // generate new account
        Account account = Account.generate(password);
        System.out.println("Account:\n" + account);

        // get private key hex
        String privateKeyHex = account.getPrivateKeyHex(password);
        System.out.println(privateKeyHex);
    }

    @Test
    public void changePassword() {
        String password = "Ambisafe";

        // generate new account
        Account account = Account.generate(password);
        System.out.println("Account:\n" + account);

        // change password
        String newPassword = "New_Pass";
        account.changePassword(password, newPassword);

        try {
            // if we try to decrypt with old password - CryptoException will be thrown
            System.out.println("Try to decrypt with wrong password...");
            String privateKeyHex = account.getPrivateKeyHex(password);
        } catch (CryptoException e) {
            System.out.println("Error: wrong password!");
        }

        try {
            // no CryptoException with right password
            System.out.println("Try to decrypt with right password...");
            String privateKeyHex = account.getPrivateKeyHex(newPassword);
            System.out.println("Success: right password!");
        } catch (CryptoException e) {
            System.out.println("This will not happen");
        }
    }
}
