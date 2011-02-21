package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.MatchPrefixesValidator;

/**
 * This annotation has to be placed on type level and is only applicable for a
 * Datacentre.
 * 
 * It checks if list of prefixes is a subset of allocator's prefixes
 */
@Documented
@Constraint(validatedBy = MatchPrefixesValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchPrefixes {
    public abstract String message() default "{org.datacite.mds.validation.constraints.MatchPrefixes.message}";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}