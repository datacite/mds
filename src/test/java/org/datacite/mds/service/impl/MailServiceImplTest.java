package org.datacite.mds.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.datacite.mds.mail.MailMessage;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class MailServiceImplTest {

    @Autowired
    private MailServiceImpl service;

    private MailMessage mail;

    private MailSender mockMailSender;

    @Before
    public void init() {
        mail = new MailMessage();
        mockMailSender = EasyMock.createMock(MailSender.class);
        service.mailSender = mockMailSender;
    }

    @After
    public void after() {
        EasyMock.verify(mockMailSender);
    }

    @Test
    public void send() {
        mockMailSender.send(mail);
        EasyMock.replay(mockMailSender);
        service.send(mail);
    }

    @Test(expected = MailException.class)
    public void sendMailException() {
        mockMailSender.send(mail);
        MailException ex = new MailSendException("foobar");
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(mockMailSender);
        service.send(mail);
    }

    @Test
    public void sendAsync() throws Exception {
        mockMailSender.send(mail);
        EasyMock.replay(mockMailSender);
        Future<Object> result = service.sendAsync(mail);
        result.get();
    }

    @Test(expected = MailException.class)
    public void sendAsyncMailException() throws Throwable {
        mockMailSender.send(mail);
        MailException ex = new MailSendException("foobar");
        EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(mockMailSender);
        Future<Object> result = service.sendAsync(mail);
        try {
            result.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

}