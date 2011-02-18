package org.datacite.mds.service;

import org.datacite.mds.mail.MailMessage;

public interface MailService {

    void send(MailMessage mail);
    
}
