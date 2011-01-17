package org.datacite.mds.service.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;


public class PasswordEncoderImplTest {

    PasswordEncoderImpl passwordEncoder;
    final static String SALT = "dummysalt";
    final static String RAW_PASS = "test_pass";

    @Before
    public void init() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        passwordEncoder = new PasswordEncoderImpl();
        Field saltField = PasswordEncoderImpl.class.getDeclaredField("salt");
        saltField.setAccessible(true);
        saltField.set(passwordEncoder, SALT);
    }
    
    @Test 
    public void encodePassword_algorithm() {
        String encPass = DigestUtils.sha256Hex(RAW_PASS + "{" + SALT + "}");
        assertEquals(encPass, passwordEncoder.encodePassword(RAW_PASS, null));
    }
    
    @Test
    public void encodePassword_globalSalt() {
        String encPass1 = passwordEncoder.encodePassword(RAW_PASS, "salt");
        String encPass2 = passwordEncoder.encodePassword(RAW_PASS, "another salt");
        assertEquals(encPass1, encPass2);
    }
    
    @Test
    public void isPasswordValid() {
        String encPass = passwordEncoder.encodePassword(RAW_PASS, "salt");
        assertTrue(passwordEncoder.isPasswordValid(encPass, RAW_PASS, "another salt"));
        assertFalse(passwordEncoder.isPasswordValid(encPass, "wrong" + RAW_PASS, "another salt"));
    }
    
    

}
