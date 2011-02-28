package org.datacite.mds.mail;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.StringUtils;

public class MailMessage extends SimpleMailMessage {

    AllocatorOrDatacentre user;

    public void loadTemplate(Resource resource) {
        String text;
        try {
            text = FileUtils.readFileToString(resource.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadTemplate(text);
    }

    public void loadTemplate(String template) {
        String[] split = template.split("\n", 2);
        setSubject(split[0]);
        setText(split[1]);
    }

    public String getTemplate() {
        return getSubject() + "\n" + getText();
    }

    public void replacePlaceholder(String placeholder, String replacement) {
        String template = getTemplate();
        template = StringUtils.replace(template, "%" + placeholder + "%", replacement);
        loadTemplate(template);
    }

    public AllocatorOrDatacentre getUser() {
        return user;
    }

    public void setUser(AllocatorOrDatacentre user) {
        this.user = user;
    }

    @Override
    public String toString() {
        if (user == null) {
            return getTo() + ": " + getSubject();
        } else {
            return user.getSymbol() + " (" + user.getContactName() + " <"
                    + user.getContactEmail() + ">): " + getSubject();
        }
    }
}
