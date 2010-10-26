package org.datacite.mds.service.impl;

import java.io.UnsupportedEncodingException;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AdminRecord;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.CreateHandleRequest;
import net.handle.hdllib.Encoder;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.SecretKeyAuthenticationInfo;

import org.apache.log4j.Logger;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HandleServiceImpl implements HandleService {

    @Value("${handle.index}") private int adminIndex;

    @Value("${handle.id}") private String adminId;

    @Value("${handle.password}") private String adminPassword;

    @Value("${handle.traceMessages}") private boolean traceMessages;

    private static final int URL_RECORD_INDEX = 1;

    private static final int ADMIN_RECORD_INDEX = 100;

    private static final String DEFAULT_ENCODING = "UTF8";

    static final Logger log4j = Logger.getLogger(HandleServiceImpl.class);

    public void create(String doi, String url) throws HandleException {
        if (doi == null || url == null || "".equals(doi) || "".equals(url))
            throw new IllegalArgumentException("DOI and URL cannot be empty");

        HandleResolver resolver = new HandleResolver();
        resolver.traceMessages = traceMessages;
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        try {
            log4j.debug("creating Handle: DOI: " + doi + " URL: " + url);

            AdminRecord admin = new AdminRecord(adminId.getBytes(DEFAULT_ENCODING), adminIndex, true, true, true, true,
                    true, true, true, true, true, true, true, true);

            HandleValue[] val = {
                    new HandleValue(ADMIN_RECORD_INDEX, "HS_ADMIN".getBytes(DEFAULT_ENCODING),
                            Encoder.encodeAdminRecord(admin), HandleValue.TTL_TYPE_RELATIVE, 86400, timestamp, null,
                            true, true, true, false),

                    new HandleValue(URL_RECORD_INDEX, "URL".getBytes(DEFAULT_ENCODING), url.getBytes(DEFAULT_ENCODING),
                            HandleValue.TTL_TYPE_RELATIVE, 86400, timestamp, null, true, true, true, false) };

            AuthenticationInfo authInfo = new SecretKeyAuthenticationInfo(adminId.getBytes(DEFAULT_ENCODING),
                    adminIndex, adminPassword.getBytes(DEFAULT_ENCODING));

            CreateHandleRequest req = new CreateHandleRequest(doi.getBytes(DEFAULT_ENCODING), val, authInfo);

            AbstractResponse response = resolver.processRequest(req);

            String msg = AbstractMessage.getResponseCodeMessage(response.responseCode);
            log4j.debug("response code from Handle request: " + msg);

            if (response.responseCode != AbstractMessage.RC_SUCCESS) {
                throw new HandleException(msg);
            }
        } catch (UnsupportedEncodingException e) {
            log4j.error("UnsupportedEncodingException", e);
            throw new RuntimeException("UnsupportedEncodingException", e);
        } catch (net.handle.hdllib.HandleException e) {
        	String message = "tried to register handle " + doi + " but failed: " + e.getMessage();
            log4j.error(message, e);
            throw new HandleException(message, e);
        }
    }

    public void update(String doi, String newUrl) throws HandleException {
        if (doi == null || newUrl == null || "".equals(doi) || "".equals(newUrl))
            throw new IllegalArgumentException("DOI and URL cannot be empty");

        log4j.debug("update Handle: DOI: " + doi + " URL: " + newUrl);

        HandleResolver resolver = new HandleResolver();
        resolver.traceMessages = traceMessages;

        try {
            HandleValue[] val = resolver.resolveHandle(doi, new String[] { "URL" }, null);
            log4j.debug("found handle: " + val);

            if (val.length != 1) {
                String msg = "Handle not found";
                log4j.debug(msg);
                throw new HandleException(msg);
            }

            val[0].setData(newUrl.getBytes(DEFAULT_ENCODING));

            AuthenticationInfo authInfo = new SecretKeyAuthenticationInfo(adminId.getBytes(DEFAULT_ENCODING),
                    adminIndex, adminPassword.getBytes(DEFAULT_ENCODING));

            ModifyValueRequest req = new ModifyValueRequest(doi.getBytes(DEFAULT_ENCODING), val, authInfo);

            AbstractResponse response = resolver.processRequest(req);

            String msg = AbstractMessage.getResponseCodeMessage(response.responseCode);
            
            log4j.debug("response code from Handle request: " + msg);

            if (response.responseCode != AbstractMessage.RC_SUCCESS) {
                throw new HandleException(msg);
            }
        } catch (net.handle.hdllib.HandleException e) {
        	String message = "tried to update handle " + doi + " but failed: " + e.getMessage();
            log4j.warn(message, e);
            throw new HandleException(message, e);
        } catch (UnsupportedEncodingException e) {
            log4j.error("UnsupportedEncodingException", e);
            throw new RuntimeException("UnsupportedEncodigException", e);
        }
    }
}
