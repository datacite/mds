package org.datacite.mds.web.ui.controller;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.mail.MailMessage;
import org.datacite.mds.mail.MailMessageFactory;
import org.datacite.mds.service.MailService;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.web.ui.model.ChangePasswordMailModel;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class ChangePasswordMailControllerTest {
    
    ChangePasswordMailController controller;
    ChangePasswordMailModel changePasswordMailModel;
    
    Allocator allocator;
    String symbol = "AL";
    
    Model model;
    BindingResult result;
    
    @Autowired
    MailMessageFactory mailMessageFactory;
    
    MailService mockMailService;
    
    @Before
    public void init() {
        controller = new ChangePasswordMailController();
        controller.mailMessageFactory = mailMessageFactory;
        mockMailService = createMock(MailService.class);
        controller.mailService = mockMailService;
        
        changePasswordMailModel = new ChangePasswordMailModel();
        changePasswordMailModel.setSymbol(symbol);
        
        model = new ExtendedModelMap();
        result = new BeanPropertyBindingResult(changePasswordMailModel, "");
        
        allocator = TestUtils.createAllocator(symbol);
        allocator.persist();
    }

    @Test
    public void createForm() {
        String view = controller.createForm(model);
        Assert.assertEquals("password/mail", view);
    }
    
    @Test
    public void mail() {
        Capture<MailMessage> capturedMail = new Capture<MailMessage>();
        mockMailService.send(and(capture(capturedMail),anyObject(MailMessage.class)));
        replay(mockMailService);
        String view = controller.mail(changePasswordMailModel, result, model);
        Assert.assertEquals("password/mail/success", view);
        checkTo(capturedMail.getValue());
        verify(mockMailService);
    }
    
    void checkTo(MailMessage mail) {
        Assert.assertEquals(1, mail.getTo().length);
        Assert.assertEquals(allocator.getContactEmail(), mail.getTo()[0]);
    }
    
    @Test
    public void mailNonExistingSymbol() {
        result.addError(new ObjectError("foo", "bar"));
        String view = controller.mail(changePasswordMailModel, result, model);
        Assert.assertEquals("password/mail", view);
    }

}
