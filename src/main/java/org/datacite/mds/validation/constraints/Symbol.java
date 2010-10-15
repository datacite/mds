package org.datacite.mds.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.datacite.mds.validation.constraints.impl.SymbolValidator;

@Documented
@Constraint(validatedBy = SymbolValidator.class)
@Target( { ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Symbol {
    public enum Type {
        ALLOCATOR, DATACENTRE
    }

    static final HashMap<Type, Pattern> PATTERNS = new HashMap<Type, Pattern>() {
        {
            put(Type.ALLOCATOR, Pattern.compile("[A-Z]{2,8}"));
            put(Type.DATACENTRE, Pattern.compile("[A-Z]{2,8}\\.[A-Z]{2,8}"));
        }
    };

    static final HashMap<Type, String> MESSAGES = new HashMap<Type, String>() {
        {
            put(Type.ALLOCATOR, "No Allocator");
            put(Type.DATACENTRE, "No Datacentre");
        }
    };

    public abstract Type[] value();

    public abstract String message() default "";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}