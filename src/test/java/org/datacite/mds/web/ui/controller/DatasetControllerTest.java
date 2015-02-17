package org.datacite.mds.web.ui.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.test.TestUtils;
import org.datacite.mds.util.SecurityUtils;
import org.datacite.mds.validation.ValidationHelper;
import org.datacite.mds.web.ui.model.CreateDatasetModel;
import org.easymock.EasyMock;
import org.junit.After;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class DatasetControllerTest {
    
    Datacentre datacentre;
    Datacentre datacentre2;
    
    DatasetController controller;

    @Autowired
    ValidationHelper validationHelper;
    
    private HandleService mockHandleService;
    
    CreateDatasetModel createDatasetModel;
    BindingResult result;
    Model model;
    
    String doi = "10.5072/TEST";
    String url = "http://example.com";
    byte[] xml;
    byte[] xml2;
    
    @Before
    public void init() {
        controller = new DatasetController();
        controller.validationHelper = validationHelper;
        controller.metadataRequired = false;
        
        mockHandleService = EasyMock.createMock(HandleService.class);
        controller.handleService = mockHandleService;
        expectNoHandleServiceCall();
        
        datacentre = TestUtils.createDefaultDatacentre("10.5072");
        datacentre2 = TestUtils.createDatacentre("AL.DC2", datacentre.getAllocator());
        datacentre2.persist();
        TestUtils.login(datacentre);

        xml = TestUtils.setDoiOfMetadata(TestUtils.getTestMetadata21(), doi);
        xml2 = TestUtils.setDoiOfMetadata(TestUtils.getTestMetadata31(), doi);
        assertTrue(!ArrayUtils.isEquals(xml, xml2));

        createDatasetModel = new CreateDatasetModel();
        createDatasetModel.setDatacentre(datacentre);
        createDatasetModel.setDoi(doi);
        createDatasetModel.setUrl(url);
        createDatasetModel.setXml(xml);

        result = new BeanPropertyBindingResult(createDatasetModel, "createDatasetModel");
        model = new ExtendedModelMap();
    }

    @After
    public void verify() {
        EasyMock.verify(mockHandleService);
    }

    private void expectNoHandleServiceCall() {
        EasyMock.reset(mockHandleService);
        EasyMock.replay(mockHandleService);
    }

    private void expectHandleServiceCreate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.create(doi, url);
        EasyMock.replay(mockHandleService);
    }
    
    private void expectHandleServiceCreateAfterUpdate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.update(doi, url);
        EasyMock.expectLastCall().andThrow(new HandleException("handle exists"));
        mockHandleService.create(doi, url);
        EasyMock.replay(mockHandleService);
    }
    
    private void expectHandleServiceResolve(String doi, String url) throws Exception {
        EasyMock.reset(mockHandleService);
        EasyMock.expect(mockHandleService.resolve(doi)).andReturn(url);
        EasyMock.replay(mockHandleService);
    }
    
    @Test
    public void create() throws Exception {
        assertCreateSuccess();
        assertEquals(1, Metadata.countMetadatas());
        Metadata metadata = Metadata.findAllMetadatas().get(0);
        assertArrayEquals(xml, metadata.getXml());
    }
    
    @Test
    public void createUploadMetadata() throws Exception {
        createDatasetModel.setXml(xml);
        assertCreateSuccess();
        assertEquals(1, Metadata.countMetadatas());
        Metadata metadata = Metadata.findAllMetadatas().get(0);
        assertArrayEquals(xml, metadata.getXml());
    }
    
    @Test
    public void createWithoutMetadata() throws Exception {
        createDatasetModel.setXml(null);
        assertCreateSuccess();
        assertEquals(0, Metadata.countMetadatas());
    }

    @Test
    public void createMetadataRequired() throws Exception {
        controller.metadataRequired = true;
        createDatasetModel.setXml(null);
        assertCreateFailure();
    }

    @Test
    public void createDoiMalformed() throws Exception {
        createDatasetModel.setDoi("foobar");
        assertCreateFailure();
    }

    @Test
    public void createUrlMalformed() throws Exception {
        createDatasetModel.setUrl("foobar");
        assertCreateFailure();
    }

    @Test
    public void createBadXml() throws Exception {
        createDatasetModel.setXml("foo".getBytes());
        assertCreateFailure();
    }
    
    @Test
    public void createTestHandlingOfXmlUploaded() throws Exception {
        createDatasetModel.setDoi(null); // force failure
        createDatasetModel.setXmlUpload(xml2);
        assertArrayEquals(xml, createDatasetModel.getXml());
        assertCreateFailure();
        assertArrayEquals(xml2, createDatasetModel.getXml());
    }

    public void assertCreateSuccess() throws Exception {
        expectHandleServiceCreate(doi, url);
        String view = controller.create(createDatasetModel, result, model);
        assertTrue(view.startsWith("redirect"));
        assertEquals(1, Dataset.countDatasets());
    }

    public void assertCreateFailure() throws Exception {
        String view = controller.create(createDatasetModel, result, model);
        assertEquals("datasets/create", view);
        assertEquals(0, Dataset.countDatasets());
        assertEquals(0, Metadata.countMetadatas());
    }   
    
    @Test
    public void updateKeepMintedTimestamp() throws Exception {
        assertCreateSuccess();
        Dataset dataset = Dataset.findDatasetByDoi(doi);
        dataset.setUrl(url + "/foo");
        Date minted = dataset.getMinted();

        expectHandleServiceCreateAfterUpdate(doi, dataset.getUrl());
        controller.update(dataset, result, model);
        
        assertEquals(minted, dataset.getMinted());
    }
    
    @Test(expected = SecurityException.class)
    public void updateWrongDatacentre() throws Exception {
        assertCreateSuccess();
        Dataset dataset = Dataset.findDatasetByDoi(doi);
        TestUtils.login(datacentre2);

        controller.update(dataset, result, model);
    }
    
    @Test(expected = SecurityException.class)
    public void updateDoi() throws Exception {
        assertCreateSuccess();
        Dataset dataset = Dataset.findDatasetByDoi(doi);
        Dataset dataset2 = new Dataset();
        dataset2.setId(dataset.getId());
        dataset2.setDoi(dataset.getDoi() + "foo");

        controller.update(dataset2, result, model);
    }
    
    @Test
    public void show() throws Exception {
        assertCreateSuccess();
        Dataset dataset = Dataset.findDatasetByDoi(doi);

        expectHandleServiceResolve(doi, url);
        controller.show(dataset.getId(), model);
        assertEquals(dataset.getId(), model.asMap().get("itemId"));
        assertEquals(url, model.asMap().get("resolvedUrl"));
    }
    
    @Test(expected = SecurityException.class)
    public void showWrongDatacentre() throws Exception {
        assertCreateSuccess();
        Dataset dataset = Dataset.findDatasetByDoi(doi);
        TestUtils.login(datacentre2);
        
        controller.show(dataset.getId(), model);
    }
    
}
