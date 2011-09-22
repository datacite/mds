package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * This annotation is used for a String containing a URL. It checks if the URL
 * is well-formed and have one of http, https and ftp as protocol. Null is a
 * valid URL (use @NotNull annotation if you don't want this).
 */
@Documented
@Size(max = 255)
@org.hibernate.validator.constraints.URL
@Pattern(regexp = "(https?|ftp)://.*|\\s*", message = "{org.datacite.mds.validation.constraints.URL.protocol.message}")
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface URL {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
