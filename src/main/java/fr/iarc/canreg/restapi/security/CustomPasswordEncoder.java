package fr.iarc.canreg.restapi.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CustomPasswordEncoder  implements PasswordEncoder {
    @Override
    public String encode(CharSequence plainTextPassword) {
        return String.valueOf(plainTextPassword);
    }

    @Override
    public boolean matches(CharSequence plainTextPassword, String passwordInDatabase) {
        MessageDigest mdSHA = null;
        MessageDigest mdSHA_256 = null;
        String cryptiSHA = null;
        String cryptiSHA_256 = null;
        try {
            mdSHA = MessageDigest.getInstance("SHA");
            mdSHA.update(String.valueOf(plainTextPassword).getBytes(StandardCharsets.UTF_8));

            byte[] raw = mdSHA.digest();
            cryptiSHA = hexEncode(raw);

            mdSHA = MessageDigest.getInstance("SHA_256");
            mdSHA.update(String.valueOf(plainTextPassword).getBytes(StandardCharsets.UTF_8));

        byte[] rawSHA_256 = mdSHA.digest(); //step 4
            cryptiSHA_256 = hexEncode(rawSHA_256);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        return cryptiSHA.equals(passwordInDatabase)||cryptiSHA_256.equals(passwordInDatabase) ;

    }

    /** This array is used to convert from bytes to hexadecimal numbers */
    static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * A convenience method to convert an array of bytes to a String.  We do
     * this simply by converting each byte to two hexadecimal digits.  Something
     * like Base 64 encoding is more compact, but harder to encode.
     *
     * @param bytes
     * @return
     */
    public static String hexEncode(byte[] bytes) {
        StringBuilder s = new StringBuilder(bytes.length * 2);
        for(int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            s.append(digits[(b & 0xf0) >> 4]);
            s.append(digits[b & 0x0f]);
        }
        return s.toString();
    }
}
