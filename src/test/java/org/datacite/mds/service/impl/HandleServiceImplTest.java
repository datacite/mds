package org.datacite.mds.service.impl;

import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class HandleServiceImplTest {

    @Autowired
    private HandleServiceImpl service;
    
    @Before
    public void setup() {
        service.dummyMode = true;
    }

    private static final String doi = "10.5072/test";

    @Test
    public void testCreate() throws HandleException {
        service.create(doi, "http://www.bl.uk/science");
    }

    @Test
    public void testUpdate() throws HandleException {
        service.update(doi, "http://www.bl.uk/datasets");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatefail() throws HandleException {
        service.update(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatefail() throws HandleException {
        service.update(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdatefail2() throws HandleException {
        service.update("","");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatefail2() throws HandleException {
        service.update("","");
    }
}
