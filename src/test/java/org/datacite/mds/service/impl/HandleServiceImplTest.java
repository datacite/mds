package org.datacite.mds.service.impl;

import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class HandleServiceImplTest {
    
    @Autowired
    private HandleService service;
    
    private static final String doi = "10.5072/test-" + (int) (System.currentTimeMillis() / 1000);;
    
    @Test
    public void testCreate() throws HandleException {
        service.create(doi, "http://www.bl.uk/science");
    }
    
    @Test(expected = HandleException.class)
    public void testCreateFail() throws HandleException {
        service.create("10.0001/1", "http://www.bl.uk/science");
    }
    
    @Test
    public void testUpdate() throws HandleException {
        service.update(doi, "http://www.bl.uk/datasets");
    }
    
    @Test(expected = HandleException.class)
    public void testUpdatefail() throws HandleException {
        service.update("10.5072/test-99999999999999999999999", "http://www.bl.uk/datasets");
    }
}
