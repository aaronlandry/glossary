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
@Constraint(validatedBy = NoDuplicateCategoriesValidator.class)
public @interface NoDuplicateCategories {
    
    String message() default "Categories cannot be selected more than once.  You cannot select a Category and its parent.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
