package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.MatchDoiPrefixValidator;

/**
 * This annotation has to be placed on type level and is only applicable for a
 * Dataset.
 * 
 * It checks if prefix of the specified DOI is in the list of the allowed
 * prefixes of the datacentre.
 * 
 * @see org.datacite.mds.domain.Dataset
 */
@Documented
@Constraint(validatedBy = MatchDoiPrefixValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDoiPrefix {
    String message() default "{org.datacite.mds.validation.constraints.MatchDoiPrefix.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
