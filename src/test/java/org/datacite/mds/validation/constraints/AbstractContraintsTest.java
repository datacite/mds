package org.datacite.mds.validation.constraints;

import org.datacite.mds.validation.ValidationHelper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public abstract class AbstractContraintsTest {
    @Autowired
    private ValidationHelper validationHelper;
    
    public ValidationHelper getValidationHelper() {
        return validationHelper;
    }
}
