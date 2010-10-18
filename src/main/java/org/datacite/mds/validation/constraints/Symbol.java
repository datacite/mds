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

/**
 * This annotation is used for any "symbol" field: Allocator symbol (e.g. "BL")
 * or datacentre symbol (e.g. "BL.SOUNDS").
 * 
 * Accept only datacentres symbol:
 * 
 * <pre>
 * &#064;Symbol(Type.DATACENTRE)
 * String symbol;
 * </pre>
 * 
 * Accept both symbol types:
 * 
 * <pre>
 * &#064;Symbol( { Symbol.Type.DATACENTRE, Symbol.Type.ALLOCATOR })
 * String symbol;
 * </pre>
 * 
 * It also possible to ensure the symbol is already in the database.
 * 
 * <pre>
 *   &#064;Symbol(hasToExist = true; value = {Symbol.Type.DATACENTRE});
 *   String symbol;
 * </pre>
 * 
 * The appropriate tables for lookup are chosen based on the given symbol types
 * 
 * @see org.datacite.mds.validation.constraints.Symbol.Type
 */
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

    /**
     * the symbol types to accept
     */
    public abstract Type[] value();

    /**
     * do a database lookup for existance of the symbol
     */
    public abstract boolean hasToExist() default false;

    public abstract String message() default "";

    public abstract Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}