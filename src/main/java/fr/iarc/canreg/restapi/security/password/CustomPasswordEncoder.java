package fr.iarc.canreg.restapi.security.password;

import canreg.common.PasswordService;
import canreg.exceptions.SystemUnavailableException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Custom password encoder that uses the password encoding mechanisms of CanReg. 
 */
public class CustomPasswordEncoder implements PasswordEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPasswordEncoder.class);
    
    @Override
    public String encode(CharSequence plainTextPassword) {
        return String.valueOf(plainTextPassword);
    }

    @Override
    public boolean matches(CharSequence plainTextPassword, String passwordInDatabase) {
        try {
          return plainTextPassword != null
              && plainTextPassword.length() > 0
              && StringUtils.isNotBlank(passwordInDatabase)
              && (
                  // First try with SHA-256
                 passwordInDatabase
                     .equals(PasswordService.getInstance().encrypt(String.valueOf(plainTextPassword), "SHA-256"))
                  // then with SHA (should not happen with new users)    
              || passwordInDatabase
                     .equals(PasswordService.getInstance().encrypt(String.valueOf(plainTextPassword), "SHA"))
              );
        } catch (SystemUnavailableException e) {
            LOGGER.error("Exception while encoding password", e);
            // go on = the authentication will fail
        }
        return false;

    }

}
