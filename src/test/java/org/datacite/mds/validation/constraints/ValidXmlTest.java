package org.datacite.mds.validation.constraints;

import static org.junit.Assert.*;

import java.io.IOException;

import org.datacite.mds.test.TestUtils;
import org.junit.Test;

public class ValidXmlTest extends AbstractContraintsTest {

    @ValidXML
    byte[] xml;

    @Test
    public void test() throws IOException {
        assertTrue(isValid(TestUtils.getTestMetadata()));
        assertFalse(isValid("<a></b>"));
    }
    
    boolean isValid(String xml) {
        this.xml = xml.getBytes();
        return super.isValid(this, "xml");
    }

}
