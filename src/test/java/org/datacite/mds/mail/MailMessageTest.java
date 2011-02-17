package org.datacite.mds.mail;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MailMessageTest {

    MailMessage mail;

    @Value("template/TestMail")
    Resource resourceTemplate;

    @Value("foo/bar")
    Resource nonExistingResource;

    @Before
    public void init() {
        mail = new MailMessage();
    }

    @Test
    public void loadTemplateAsResource() {
        mail.loadTemplate(resourceTemplate);
        assertEquals("Subject\nText", mail.getTemplate());
    }

    @Test(expected = RuntimeException.class)
    public void loadTemplateNonExisting() {
        mail.loadTemplate(nonExistingResource);
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
