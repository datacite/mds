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
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.MagicAuthStringService;
import org.springframework.stereotype.Service;

@Service
public class MagicAuthStringServiceImpl implements MagicAuthStringService {
    Logger log4j = Logger.getLogger(MagicAuthStringServiceImpl.class);
    
    private String getBaseAuthString(String symbol) {
        String baseAuthString = null;
        Datacentre datacentre = Datacentre.findDatacentreBySymbol(symbol);
        if (datacentre != null) {
            baseAuthString = datacentre.getBaseAuthString();
        } else {
            Allocator allocator = Allocator.findAllocatorBySymbol(symbol);
            if (allocator != null) {
                baseAuthString = allocator.getBaseAuthString();
            }
        }
        log4j.debug("base auth string for " + symbol + ": " + baseAuthString);
        return baseAuthString;
    }

    private String saltAuthStringWithDate(String auth, Date date) {
        if (StringUtils.isEmpty(auth)) {
            return null;
        }
        // DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd mm");
        return DigestUtils.sha256Hex(auth + df.format(date));
    }

    public Collection<String> getValidAuthStrings(String symbol) {
        List<String> list = new ArrayList<String>();
        Date curDate = new Date();
        // Date prevDate = DateUtils.addDays(curDate, -1);
        Date prevDate = DateUtils.addMinutes(curDate, -1);
        String baseAuthString = getBaseAuthString(symbol);
        if (baseAuthString != null) {
            list.add(saltAuthStringWithDate(baseAuthString, curDate));
            list.add(saltAuthStringWithDate(baseAuthString, prevDate));
        }
        log4j.debug("valid auth strings for " + symbol + ": " + list);
        return list;
    }

    public String getCurrentAuthString(String symbol) {
        return saltAuthStringWithDate(getBaseAuthString(symbol), new Date());
    }
    
    public String getCurrentAuthString(Datacentre datacentre) {
        return saltAuthStringWithDate(datacentre.getBaseAuthString(), new Date());
    }

    public String getCurrentAuthString(Allocator allocator) {
        return saltAuthStringWithDate(allocator.getBaseAuthString(), new Date());
    }

    public boolean isValidAuthString(String symbol, String auth) {
        return !StringUtils.isEmpty(symbol) && !StringUtils.isEmpty(auth) && // 
                getValidAuthStrings(symbol).contains(auth);
    }
}
