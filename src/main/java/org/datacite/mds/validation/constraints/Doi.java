package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

import org.datacite.mds.validation.constraints.impl.DoiValidator;

/**
 * This annotation is used for a String containing a DOI. It checks if the DOI
 * is well-formed. Null is a valid DOI (use @NotNull annotation if you don't
 * want this).
 */
@Documented
@Constraint(validatedBy = DoiValidator.class)
@Size(max = 255)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Doi {
    String message() default "{org.datacite.mds.validation.constraints.Doi.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}