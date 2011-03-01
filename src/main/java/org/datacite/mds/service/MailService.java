package org.datacite.mds.service;

import java.util.concurrent.Future;

import org.datacite.mds.mail.MailMessage;

public interface MailService {

    void send(MailMessage mail);

    Future<Object> sendAsync(MailMessage mail);
    
}
