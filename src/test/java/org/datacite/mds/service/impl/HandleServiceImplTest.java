package org.datacite.mds.service.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractRequest;
import net.handle.hdllib.GenericResponse;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

import org.datacite.mds.service.HandleException;
import org.datacite.mds.web.api.NotFoundException;
import org.easymock.IExpectationSetters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class HandleServiceImplTest {

    @Autowired
    private HandleServiceImpl service;
    
    private static final String doi = "10.5072/test";
    private static final String url = "http://example.com";
    

    @Before
    public void setup() {
        service.dummyMode = false;
        service.resolver = createMock(HandleResolver.class);
    }
    
    @After
    public void tearDown() {
        service.dummyMode = true;
        verify(service.resolver);
    }
    
    @Test
    public void testResolve() throws Exception {
        mockResolveExistingHandle();
        replay(service.resolver);
        assertEquals(url, service.resolve(doi));
    }

    @Test(expected = NotFoundException.class)
    public void testResolveNonExistingUrl() throws Exception {
        mockResolveNonExistingHandle();
        replay(service.resolver);
        service.resolve(doi);
    }

    @Test(expected = NotFoundException.class)
    public void testResolveNonExisting() throws Exception {
        expectResolveHandle().andThrow(new net.handle.hdllib.HandleException(net.handle.hdllib.HandleException.HANDLE_DOES_NOT_EXIST));
        replay(service.resolver);
        service.resolve(doi);
    }

    @Test(expected = HandleException.class)
    public void testResolveHandleException() throws Exception {
        expectResolveHandle().andThrow(new net.handle.hdllib.HandleException(net.handle.hdllib.HandleException.SERVER_ERROR));
        replay(service.resolver);
        service.resolve(doi);
    }

    private IExpectationSetters<HandleValue[]> expectResolveHandle() throws net.handle.hdllib.HandleException  {
        return expect(service.resolver.resolveHandle(eq(doi), anyObject(String[].class), anyObject(int[].class)));
    }

    @Test
    public void testCreate() throws Exception {
        mockResponseCode(AbstractMessage.RC_SUCCESS);
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testCreateError() throws Exception {
        mockResponseCode(AbstractMessage.RC_ERROR);
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testCreateException() throws Exception {
        mockResponseException();
        replay(service.resolver);
        service.create(doi, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyDoi() throws HandleException {
        replay(service.resolver);
        service.create("", url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullDoi() throws HandleException {
        replay(service.resolver);
        service.create(null, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyUrl() throws HandleException {
        replay(service.resolver);
        service.create(doi, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullUrl() throws HandleException {
        replay(service.resolver);
        service.create(doi, null);
    }

    @Test
    public void testUpdate() throws Exception {
        mockResponseCode(AbstractMessage.RC_SUCCESS);
        replay(service.resolver);
        service.update(doi, url);
    }

    @Test(expected = HandleException.class)
    public void testUpdateError() throws Exception {
        mockResponseCode(AbstractMessage.RC_ERROR);
        replay(service.resolver);
        service.update(doi, url);
    }
    
    @Test(expected = HandleException.class)
    public void testUpdateException() throws Exception {
        mockResponseException();
        replay(service.resolver);
        service.update(doi, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmptyDoi() throws HandleException {
        replay(service.resolver);
        service.update("", url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullDoi() throws HandleException {
        replay(service.resolver);
        service.update(null, url);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEmptyUrl() throws HandleException {
        replay(service.resolver);
        service.update(doi, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNullUrl() throws HandleException {
        replay(service.resolver);
        service.update(doi, null);
    }
    
    void mockResponseCode(int respondeCode) throws net.handle.hdllib.HandleException {
        GenericResponse response = new GenericResponse(0, respondeCode);
        expect(service.resolver.processRequest(anyObject(AbstractRequest.class))).andReturn(response);
    }

    void mockResponseException() throws net.handle.hdllib.HandleException {
        net.handle.hdllib.HandleException ex = new net.handle.hdllib.HandleException(0);
        expect(service.resolver.processRequest(anyObject(AbstractRequest.class))).andThrow(ex);
    }

    void mockResolveExistingHandle() throws net.handle.hdllib.HandleException {
        HandleValue[] values = { new HandleValue(1, "URL".getBytes(), url.getBytes()) };
        expectResolveHandle().andReturn(values);
    }

    void mockResolveNonExistingHandle() throws net.handle.hdllib.HandleException {
        HandleValue[] values = {};
        expectResolveHandle().andReturn(values);
    }

}
