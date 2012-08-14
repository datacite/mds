package org.datacite.mds.web.ui.controller;

import static org.junit.Assert.assertEquals;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.HandleService;
import org.datacite.mds.test.TestUtils;
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
    
    DatasetController controller;

    @Autowired
    ValidationHelper validationHelper;
    
    private HandleService mockHandleService;
    
    CreateDatasetModel createDatasetModel;
    BindingResult result;
    Model model;
    
    
    @Before
    public void init() {
        controller = new DatasetController();
        controller.validationHelper = validationHelper;
        
        mockHandleService = EasyMock.createMock(HandleService.class);
        controller.handleService = mockHandleService;
        expectNoHandleServiceCall();
        
        datacentre = TestUtils.createDefaultDatacentre("10.5072");
        TestUtils.login(datacentre);
        
        createDatasetModel = new CreateDatasetModel();
        createDatasetModel.setDatacentre(datacentre);
        result = new BeanPropertyBindingResult(createDatasetModel, "createDatasetModel");
        model = new ExtendedModelMap();
    }
    
    private void expectNoHandleServiceCall() {
        EasyMock.reset(mockHandleService);
        EasyMock.replay(mockHandleService);
    }

    @After
    public void verify() {
        EasyMock.verify(mockHandleService);
    }

    @Test
    public void createEmptyForm() {
        String view = controller.create(createDatasetModel, result, model);
        assertEquals("datasets/create", view);
        assertEquals(0, Dataset.countDatasets());
    }
    
    private void expectHandleServiceCreate(String doi, String url) throws HandleException {
        EasyMock.reset(mockHandleService);
        mockHandleService.create(doi, url);
        EasyMock.replay(mockHandleService);
    }

}
