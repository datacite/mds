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
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.ValidXML;
import org.springframework.beans.factory.annotation.Configurable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@Configurable
public class ValidXMLValidator implements ConstraintValidator<ValidXML, byte[]> {
    Logger log = Logger.getLogger(ValidXMLValidator.class);
    
    String xsd;
     
    boolean enabled;

    public void initialize(ValidXML constraintAnnotation) {
        if (!constraintAnnotation.xsd().isEmpty()) {
            this.xsd = constraintAnnotation.xsd();
        }
        log.debug("init: xsd=" + getXsd());
        log.debug("init: enabled=" + isEnabled());
    }

    public boolean isValid(byte[] xmlBytes, ConstraintValidatorContext context) {
        InputStream inputStream = new ByteArrayInputStream(xmlBytes);
        if (! isEnabled()) {
            log.debug("validation skipped; checking only for well-formness");
            try {
                XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.parse(new InputSource(inputStream));
            } catch (Exception e) {
                ValidationUtils.addConstraintViolation(context, "xml error: " + e.getMessage());
                return false;
            }
            return true;
        }
        
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaSource = new StreamSource(xsd);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
