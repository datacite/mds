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
 * This annotation is used for XML content (byte[]) that should be validate to
 * an xml schema. The xsd parameter have a be a ULR pointing to the xsd used for
 * validation
 * 
 * Example usage:
 * 
 * <pre>
 *   &#064;ValidXML(xsd = "http://datacite.org/metadata.xsd")
 *   private byte[] xml;
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
    public abstract String xsd();

    public abstract String message() default "{org.datacite.mds.validation.constraints.ValidXML.message}";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};

}