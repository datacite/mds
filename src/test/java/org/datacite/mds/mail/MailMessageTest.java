package org.datacite.mds.mail;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MailMessageTest {
    
    MailMessage mail;
    
    @Before
    public void init() {
        mail = new MailMessage();
    }
    
    @Test
    public void loadTemplateAsString() {
        String subject = "Subject line";
        String text = "line on\n line two \n";
        String template = subject + "\n" + text;
        mail.loadTemplate(template);
        assertEquals(subject, mail.getSubject());
        assertEquals(text, mail.getText());
    }
    
    @Test
    public void replacePlaceholder() {
        mail.setSubject("%aa% - %cc%");
        mail.setText("%aa% - %bb%\n%aa%");
        mail.replacePlaceholder("aa", "A");
        mail.replacePlaceholder("bb", "B");
        assertEquals("A - %cc%", mail.getSubject());
        assertEquals("A - B\nA", mail.getText());
    }

}
