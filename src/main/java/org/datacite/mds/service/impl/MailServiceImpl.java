package org.datacite.mds.service.impl;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    Logger log4j = Logger.getLogger(MailServiceImpl.class);

    @Autowired
    MailSender mailSender;

    public void send(MailMessage mail) {
        log4j.info("Sending mail: " + mail);
        mailSender.send(mail);
    }
    
    @Async
    public Future<Object> sendAsync(MailMessage mail) {
        send(mail);
        return new AsyncResult<Object>(null);
    }

}
