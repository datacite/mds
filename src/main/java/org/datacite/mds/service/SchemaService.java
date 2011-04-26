package org.datacite.mds.service;

import javax.xml.validation.Validator;

import org.datacite.mds.domain.Metadata;
import org.datacite.mds.validation.ValidationException;
import org.xml.sax.SAXException;

public interface SchemaService {
    
    String getSchemaLocation(Metadata metadata) throws ValidationException;
    
    Validator getSchemaValidator(String schemaLocation) throws SAXException;

}
