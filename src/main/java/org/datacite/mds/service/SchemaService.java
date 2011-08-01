package org.datacite.mds.service;

import javax.validation.ValidationException;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public interface SchemaService {
    
    String getNamespace(byte[] xml) throws ValidationException;
    
    String getSchemaLocation(byte[] xml) throws ValidationException;
    
    Validator getSchemaValidator(String schemaLocation) throws SAXException;
    
    String getDoi(byte[] xml);

}
