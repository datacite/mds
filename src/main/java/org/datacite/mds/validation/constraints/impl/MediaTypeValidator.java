package org.datacite.mds.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.MediaType;

public class MediaTypeValidator implements ConstraintValidator<MediaType, String> {

    public void initialize(MediaType constraintAnnotation) {
        // nothing to initialize
    }

    public boolean isValid(String mediaTypeString, ConstraintValidatorContext context) {
        org.springframework.http.MediaType mediaType;
        try {
            mediaType = org.springframework.http.MediaType.parseMediaType(mediaTypeString);
        } catch (Exception e) {
            ValidationUtils.addConstraintViolation(context, e.getMessage());
            return false;
        }

        String constructedMediaType = mediaType.getType() + "/" + mediaType.getSubtype();
        boolean hasNoParameter = StringUtils.equals(mediaTypeString, constructedMediaType);
        boolean isWildCard = mediaType.isWildcardType() || mediaType.isWildcardSubtype();
        return hasNoParameter && !isWildCard;
    }

}
