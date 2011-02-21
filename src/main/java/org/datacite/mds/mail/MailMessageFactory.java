package org.datacite.mds.mail;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.MagicAuthStringService;
import org.datacite.mds.util.DomainUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MailMessageFactory {
    @Value("${email.mdsUrl}")
    String mdsUrl;

    @Value("${email.from}")
    String from;

    @Value("${email.replyTo}")
    String replyTo;

    @Value("classpath:template/ResetPasswordMail")
    Resource templateResetPasswordMail;
    
    @Value("classpath:template/WelcomeDatacentreMail")
    Resource templateWelcomeDatacentreMail;

    @Value("classpath:template/WelcomeAllocatorMail")
    Resource templateWelcomeAllocatorMail;

    @Autowired
    MagicAuthStringService magicAuthStringService;

    public MailMessage createResetPasswordMail(AllocatorOrDatacentre user) {
        MailMessage mail = createMailWithTemplate(user, templateResetPasswordMail);
        mail.replacePlaceholder("magicAuth", magicAuthStringService.getCurrentAuthString(user));
        return mail;
    }

    public MailMessage createWelcomeDatacentreMail(Datacentre datacentre) {
        MailMessage mail = createMailWithTemplate(datacentre, templateWelcomeDatacentreMail);
        mail.setCc(datacentre.getAllocator().getContactEmail());
        mail.replacePlaceholder("magicAuth", magicAuthStringService.getCurrentAuthString(datacentre));
        mail.replacePlaceholder("allocatorName", datacentre.getAllocator().getName());
        return mail;
    }

    public MailMessage createWelcomeAllocatorMail(Allocator allocator) {
        MailMessage mail = createMailWithTemplate(allocator, templateWelcomeAllocatorMail);
        mail.setCc(DomainUtils.getAdmin().getContactEmail());
        mail.replacePlaceholder("magicAuth", magicAuthStringService.getCurrentAuthString(allocator));
        return mail;
    }

    MailMessage createMailWithTemplate(AllocatorOrDatacentre user, Resource template) {
        MailMessage mail = createGenericMail(user);
        mail.loadTemplate(template);
        mail.replacePlaceholder("contactName", user.getContactName());
        mail.replacePlaceholder("symbol", user.getSymbol());
        mail.replacePlaceholder("mdsUrl", mdsUrl);
        return mail;
    }

    public MailMessage createGenericMail(AllocatorOrDatacentre user) {
        MailMessage mail = new MailMessage();
        mail.setUser(user);
        mail.setFrom(from);
        mail.setTo(user.getContactEmail());
        if (!StringUtils.isBlank(replyTo))
            mail.setReplyTo(replyTo);
        return mail;
    }
}
