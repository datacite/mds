package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.datacite.mds.validation.constraints.impl.UniqueValidator;

/**
 * <p>
 * This annotation checks an entity field for uniqueness. It has to be placed on
 * type level. It does <i>not</i> insert a constraint on database level. You
 * have it do it on your own (see example) and it is strongly recommended.
 * </p>
 * 
 * <p>
 * You can also check a list of entity fields. In this case the combination of
 * all provided fields must be unique.
 * </p>
 * 
 * <p>
 * It looks up for objects with the same field value in the persistence backend.
 * If there is none the constraint is valid. If there is exactly one matching
 * object we check for id field ("id" by default) matching. If so this indicates
 * an update operation and the constraint is valid. Otherwise it fails.
 * </p>
 * 
 * <p>
 * Example:
 * 
 * <pre>
 * &#064;Entity
 * &#064;Unique(field="myfield")
 * public class Domain {
 *   &#064;Id
 *   Long id;
 *   
 *   &#064;Column(unique = true)
 *   String myField;
 * }
 * 
 * </p>
 * 
 * 
 * </pre>
 */
@Documented
@Constraint(validatedBy = UniqueValidator.class)
@Target( { ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface Unique {

    /**
     * @return name(s) of the field(s) which should be unique
     */
    String[] field();

    /**
     * @return name of entity's id field (defaults to "id")
     */
    String idField() default "id";

    String message() default "{org.datacite.mds.validation.constraints.Unique.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
