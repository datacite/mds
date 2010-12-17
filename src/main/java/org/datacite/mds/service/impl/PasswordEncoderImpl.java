package org.datacite.mds.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderImpl extends ShaPasswordEncoder {

    Logger log4j = Logger.getLogger(PasswordEncoderImpl.class);

    @Value("${salt.password}") String salt;

    public PasswordEncoderImpl() {
        super(256); // SHA-256
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        // use system-wide salt
        log4j.debug("encodePassword (salt=" + this.salt + ")");
        return super.encodePassword(rawPass, this.salt);
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        // use system-wide salt
        return super.isPasswordValid(encPass, rawPass, this.salt);
    }

}
