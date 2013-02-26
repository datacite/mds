package org.datacite.mds.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.service.MagicAuthStringService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MagicAuthStringServiceImpl implements MagicAuthStringService {
    Logger log4j = Logger.getLogger(MagicAuthStringServiceImpl.class);

    @Value("${magicAuthString.validityInDays}")
    Integer validityInDays;

    @Value("${salt.magicAuthString}")
    String salt;

    private String saltAndHash(String baseAuth, Date date) {
        if (StringUtils.isEmpty(baseAuth)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String rawAuth = baseAuth + ":" + df.format(date) + ":" + this.salt;
        String hashedAuth = DigestUtils.sha256Hex(rawAuth);
        log4j.debug("saltAndHash: " + hashedAuth + " <- " + rawAuth);
        return hashedAuth;
    }

    public Collection<String> getValidAuthStrings(AllocatorOrDatacentre user) {
        List<String> list = new ArrayList<String>();
        if (user == null)
            return list;

        String baseAuthString = user.getBaseAuthString();

        Date date = new Date();
        for (int i = 0; i <= validityInDays; i++) {
            list.add(saltAndHash(baseAuthString, date));
            date = DateUtils.addDays(date, -1);
        }
        
        log4j.debug("valid auth strings for " + user.getSymbol() + ": " + list);
        return list;
    }

    public String getCurrentAuthString(AllocatorOrDatacentre user) {
        if (user == null)
            return null;
        else
            return saltAndHash(user.getBaseAuthString(), new Date());
    }

    public boolean isValidAuthString(AllocatorOrDatacentre user, String auth) {
        return getValidAuthStrings(user).contains(auth);
    }
}
