package org.datacite.mds.service;

import org.datacite.mds.domain.Metadata;
import org.datacite.mds.validation.ValidationException;

public interface SchemaService {
    
    String getSchemaLocation(Metadata metadata) throws ValidationException;

}
