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

    @Value("${salt.magicAuthString}")
    String salt;

    private String getBaseAuthString(AllocatorOrDatacentre user) {
        String baseAuthString = null;
        if (user != null) {
            baseAuthString = user.getBaseAuthString();
        }
        log4j.debug("base auth string for " + user.getSymbol() + ": " + baseAuthString);
        return baseAuthString;
    }

    private String saltAndHash(String baseAuth, Date date) {
        if (StringUtils.isEmpty(baseAuth)) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        // DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        String rawAuth = baseAuth + ":" + df.format(date) + ":" + this.salt;
        String hashedAuth = DigestUtils.sha256Hex(rawAuth);
        log4j.debug("saltAndHash: " + hashedAuth + " <- " + rawAuth);
        return hashedAuth;
    }

    public Collection<String> getValidAuthStrings(AllocatorOrDatacentre user) {
        List<String> list = new ArrayList<String>();
        Date curDate = new Date();
        Date prevDate = DateUtils.addDays(curDate, -1);
        // Date prevDate = DateUtils.addMinutes(curDate, -1);
        String baseAuthString = getBaseAuthString(user);
        if (baseAuthString != null) {
            list.add(saltAndHash(baseAuthString, curDate));
            list.add(saltAndHash(baseAuthString, prevDate));
        }
        log4j.debug("valid auth strings for " + user.getSymbol() + ": " + list);
        return list;
    }

    public String getCurrentAuthString(AllocatorOrDatacentre user) {
        return saltAndHash(getBaseAuthString(user), new Date());
    }

    public boolean isValidAuthString(AllocatorOrDatacentre user, String auth) {
        return user != null && !StringUtils.isEmpty(auth) && // 
                getValidAuthStrings(user).contains(auth);
    }
}
