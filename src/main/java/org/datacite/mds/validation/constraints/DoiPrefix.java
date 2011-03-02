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
 * This annotation is used for a String containing a DOI prefix (e.g.
 * "10.5072"). It checks if the DOI is well-formed.
 */
@Documented
@Constraint(validatedBy = {})
@Size(max = 80)
@Pattern(regexp = "10\\.(\\d)+", message = "{org.datacite.mds.validation.constraints.DoiPrefix.message}")
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DoiPrefix {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
