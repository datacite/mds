package org.datacite.mds.service.impl;

import org.apache.log4j.Logger;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    Logger log4j = Logger.getLogger(MailServiceImpl.class);

    @Autowired
    MailSender mailSender;

    public void send(MailMessage mail) {
        AllocatorOrDatacentre user = mail.getUser();
        if (user == null) {
            log4j.info("Sending mail to " + mail.getTo() + ": " + mail.getSubject());
        } else {
            log4j.info("Sending mail to " + user.getSymbol() + " (" + user.getContactName() + " <"
                    + user.getContactEmail() + ">): " + mail.getSubject());
        }
        mailSender.send(mail);
    }
    
    @Async
    public void sendAsync(MailMessage mail) {
        send(mail);
    }

}
