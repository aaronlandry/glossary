package com.superiad.glossary.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author mwhitrock
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoDuplicateTermsValidator.class)
public @interface NoDuplicateTerms {
    
    String message() default "Two terms cannot have the same name.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
