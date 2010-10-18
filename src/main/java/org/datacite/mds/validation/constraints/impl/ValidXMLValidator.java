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

import org.datacite.mds.util.Utils;
import org.datacite.mds.validation.constraints.ValidXML;

public class ValidXMLValidator implements ConstraintValidator<ValidXML, byte[]> {
    String xsd;

    public void initialize(ValidXML constraintAnnotation) {
        this.xsd = constraintAnnotation.xsd();
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
            Utils.addConstraintViolation(context, e.getMessage());
            return false;
        }

        return true;
    }
}
