package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.MatchSymbolPrefixValidator;

/**
 * This annotation has to be placed on type level and is only applicable for a
 * Datacentre.
 * 
 * It checks if prefix of the specified symbol begins with the symbol of the
 * assigned allocator
 * 
 */

@Documented
@Constraint(validatedBy = MatchSymbolPrefixValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchSymbolPrefix {
    String message() default "{org.datacite.mds.validation.constraints.MatchSymbolPrefix.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}