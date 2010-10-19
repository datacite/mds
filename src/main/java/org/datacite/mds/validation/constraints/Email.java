package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This annotation is used for a String containing a DOI. It checks if the DOI
 * is well-formed.
 */
@Documented
@Constraint(validatedBy = {})
@Size(max = 255)
@Pattern(regexp = ".+@.+\\.[^.]{2,}") // dot required in domain part, tld at least 2 chars.
@org.hibernate.validator.constraints.Email
@ReportAsSingleViolation
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    public abstract String message() default "{org.hibernate.validator.constraints.Email.message}";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}