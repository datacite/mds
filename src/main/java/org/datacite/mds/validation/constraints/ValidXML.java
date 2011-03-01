package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.datacite.mds.validation.constraints.impl.ValidXMLValidator;

/**
 * This annotation is used for XML content (byte[]) that should be validate to a
 * xml schema. The optional xsd parameter have a be a URL pointing to the xsd
 * used for validation.
 * 
 * Example usage:
 * 
 * <pre>
 * &#064;ValidXML(xsd = &quot;http://datacite.org/metadata.xsd&quot;)
 * private byte[] xml;
 * </pre>
 */
@Documented
@Constraint(validatedBy = ValidXMLValidator.class)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface ValidXML {

    /**
     * The URL to the xml schema
     * 
     * @return
     */
    String xsd() default "";

    String message() default "{org.datacite.mds.validation.constraints.ValidXML.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}