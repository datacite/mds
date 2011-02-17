package org.datacite.mds.mail;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.service.MagicAuthStringService;
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

    @Value("template/ResetPasswordMail")
    Resource templateResetPasswordMail;

    @Autowired
    MagicAuthStringService magicAuthStringService;

    public MailMessage createResetPasswordMail(AllocatorOrDatacentre user) {
        MailMessage mail = createMailWithTemplate(user, templateResetPasswordMail);
        mail.replacePlaceholder("magicAuth", magicAuthStringService.getCurrentAuthString(user));
        return mail;
    }

    MailMessage createMailWithTemplate(AllocatorOrDatacentre user, Resource template) {
        MailMessage mail = createGenericMail(user);
        mail.loadTemplate(templateResetPasswordMail);
        mail.replacePlaceholder("contactName", user.getContactName());
        mail.replacePlaceholder("symbol", user.getSymbol());
        mail.replacePlaceholder("mdsUrl", mdsUrl);
        return mail;
    }

    public MailMessage createGenericMail(AllocatorOrDatacentre user) {
        MailMessage mail = new MailMessage();
        mail.setFrom(from);
        mail.setTo(user.getContactEmail());
        if (!StringUtils.isBlank(replyTo))
            mail.setReplyTo(replyTo);
        return mail;
    }
}
