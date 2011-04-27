package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.util.Utils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MatchDoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchDoiValidator implements ConstraintValidator<MatchDoi, Metadata> {
    String defaultMessage;
    
    @Autowired
    SchemaService schemaService;

    public void initialize(MatchDoi constraintAnnotation) {
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Metadata metadata, ConstraintValidatorContext context) {
        ValidationUtils.addConstraintViolation(context, defaultMessage, "xml");
        String doiFromDataset = metadata.getDataset().getDoi();
        String doiFromXml = schemaService.getDoi(metadata.getXml());
        doiFromXml = Utils.normalizeDoi(doiFromXml);
        boolean isValid = StringUtils.equals(doiFromDataset, doiFromXml);
        return isValid;
    }
}
