package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.MatchDomainValidator;

/**
 * This annotation has to be placed on type level and is only applicable for a
 * Dataset.
 * 
 * It checks if hostname of the specified URL is in the list of the allowed
 * domains of the datacentre.
 * 
 * @see org.datacite.mds.domain.Dataset
 */
@Documented
@Constraint(validatedBy = MatchDomainValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDomain {
    String message() default "{org.datacite.mds.validation.constraints.MatchDomain.message}";
    
    String wildCard() default "*";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
