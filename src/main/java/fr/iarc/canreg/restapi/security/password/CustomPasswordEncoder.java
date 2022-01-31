package fr.iarc.canreg.restapi.security.password;

import canreg.common.PasswordService;
import canreg.exceptions.SystemUnavailableException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder {

    PasswordService passwordService;

    @Override
    public String encode(CharSequence plainTextPassword) {
        return String.valueOf(plainTextPassword);
    }

    @Override
    public boolean matches(CharSequence plainTextPassword, String passwordInDatabase) {

        String pwdSHA = null;
        String pwdSHA_256 = null;
        try {
            pwdSHA = PasswordService.getInstance().encrypt(String.valueOf(plainTextPassword), "SHA");

            pwdSHA_256 = PasswordService.getInstance().encrypt(String.valueOf(plainTextPassword), "SHA-256");
        } catch (SystemUnavailableException e) {
            e.printStackTrace();
        }


        return passwordInDatabase.equals(pwdSHA) || passwordInDatabase.equals(pwdSHA_256);

    }

}
