package com.superiad.glossary.authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Custom PasswordEncoder for the login process.  Right now this calls 
 * EncryptionUtils, which is a wrapper for BCryptPasswordEncoder.
 * @see EncryptionUtils
 * @author Aaron
 */
@Service
public class BasicPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return null;
        //return PasswordEncryptionUtils.encrypt(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return true;
        //return PasswordEncryptionUtils.matches(rawPassword,encodedPassword);
    }
    
}
