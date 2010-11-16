package org.datacite.mds.validation.constraints.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.datacite.mds.validation.constraints.ValidXML;
import org.datacite.mds.validation.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ValidXMLValidator implements ConstraintValidator<ValidXML, byte[]> {
    Logger log = Logger.getLogger(ValidXMLValidator.class);
    
    String xsd;

    public void initialize(ValidXML constraintAnnotation) {
        if (!constraintAnnotation.xsd().isEmpty()) {
            this.xsd = constraintAnnotation.xsd();
        }
        log.debug("init: xsd=" + getXsd());
    }

    public boolean isValid(byte[] xmlBytes, ConstraintValidatorContext context) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(xsd);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            InputStream inputStream = new ByteArrayInputStream(xmlBytes);
            Source xml = new StreamSource(inputStream);
            validator.validate(xml);
        } catch (Exception e) {
            ValidationUtils.addConstraintViolation(context, "xml error: " + e.getMessage());
            return false;
        }

        return true;
    }

    public String getXsd() {
        return xsd;
    }

    public void setXsd(String xsd) {
        this.xsd = xsd;
    }
}
