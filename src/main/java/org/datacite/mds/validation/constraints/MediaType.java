package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Size;

import org.datacite.mds.validation.constraints.impl.MediaTypeValidator;

/**
 * This annotation is used for a String containing a Internet Media Type (aka
 * MIME type). It checks if the media type is well-formed.
 */
@Documented
@Constraint(validatedBy = MediaTypeValidator.class)
@Size(max = 80)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MediaType {
    String message() default "{org.datacite.mds.validation.constraints.MediaType.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
