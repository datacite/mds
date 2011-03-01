package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.ListOfDomainsValidator;

/**
 * This annotation is used for a String containing a comma separated list of
 * domains (e.g. "datacite.org,datacite.org.uk")  
 */
@Documented
@Constraint(validatedBy = ListOfDomainsValidator.class)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ListOfDomains {
    String message() default "{org.datacite.mds.validation.constraints.ListOfDomains.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}