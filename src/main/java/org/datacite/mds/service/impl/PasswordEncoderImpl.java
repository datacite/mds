package org.datacite.mds.service.impl;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

public class PasswordEncoderImpl extends ShaPasswordEncoder {

    String salt = null; // disable salt for a moment

    public PasswordEncoderImpl() {
        super(256); // SHA-256
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        // use system-wide salt
        return super.encodePassword(rawPass, this.salt);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        // use system-wide salt
        return super.isPasswordValid(encPass, rawPass, this.salt);
    }

}
