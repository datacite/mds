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

import org.datacite.mds.validation.constraints.impl.EmailValidator;

/**
 * This annotation is used for email addresses. It checks if the email address
 * is well-formed.
 */
@Documented
@Constraint(validatedBy = {EmailValidator.class})
@ReportAsSingleViolation
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    public abstract String message() default "{org.hibernate.validator.constraints.Email.message}";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}