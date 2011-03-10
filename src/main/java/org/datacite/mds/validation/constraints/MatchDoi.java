package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.MatchDoiValidator;

/**
 * This annotation has to be placed on type level and is only applicable for a
 * Metadata.
 * 
 * It checks if DOI parsed from XML matches DOI from assigned dataset
 */
@Documented
@Constraint(validatedBy = MatchDoiValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDoi {
    String message() default "{org.datacite.mds.validation.constraints.MatchDoi.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
