package org.datacite.mds.web.ui.controller;

import junit.framework.Assert;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class UserInfoControllerTest {
    
    UserInfoController controller = new UserInfoController();

    Allocator allocator;
    Datacentre datacentre;

    Model model;

    @Before
    public void init() {
        allocator = Utils.createAllocator("AL");
        allocator.persist();
        datacentre = Utils.createDatacentre("AL.DC", allocator);
        datacentre.persist();
        model = new ExtendedModelMap();
    }
    
    @Test
    public void userinfoAllocator() throws Exception {
        Utils.login(allocator);
        String view = controller.userinfo(model);
        Assert.assertEquals("userinfo", view);
        checkModelAttributeAllocator();
        Assert.assertFalse(model.containsAttribute("datacentre"));
    }

    @Test
    public void userinfoDatacentre() throws Exception {
        Utils.login(datacentre);
        String view = controller.userinfo(model);
        Assert.assertEquals("userinfo", view);
        checkModelAttributeAllocator();
        checkModelAttributeDatacentre();
    }
    
    @Test
    public void userinfoNotLoggedIn() throws Exception {
        Utils.login(null);
        String view = controller.userinfo(model);
        Assert.assertEquals("userinfo", view);
        Assert.assertTrue(model.asMap().isEmpty());
    }
    
    void checkModelAttributeAllocator() {
        Allocator modelAllocator = (Allocator) model.asMap().get("allocator");
        Assert.assertNotNull(modelAllocator);
        Assert.assertEquals(allocator.getSymbol(), modelAllocator.getSymbol());
    }
    
    void checkModelAttributeDatacentre() {
        Datacentre modelDatacentre = (Datacentre) model.asMap().get("datacentre");
        Assert.assertNotNull(modelDatacentre);
        Assert.assertEquals(datacentre.getSymbol(), modelDatacentre.getSymbol());
    }
    
}
