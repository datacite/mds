package org.datacite.mds.service;

import javax.xml.validation.Validator;

import org.datacite.mds.validation.ValidationException;
import org.xml.sax.SAXException;

public interface SchemaService {
    
    String getSchemaLocation(byte[] xml) throws ValidationException;
    
    Validator getSchemaValidator(String schemaLocation) throws SAXException;

}
