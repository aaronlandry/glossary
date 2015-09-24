package com.superiad.glossary.validation;

import com.superiad.glossary.model.Category;
import com.superiad.glossary.model.Term;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author mwhitrock
 */
public class NoDuplicateCategoriesValidator implements ConstraintValidator<NoDuplicateCategories, Term> {
    
    private NoDuplicateCategories constraintAnnotation;

    @Override
    public void initialize(NoDuplicateCategories constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Term term, ConstraintValidatorContext context) {
        List<Category> categories = term.getCategories();
        if (categories == null || categories.isEmpty()) {
            // NOT SURE HOW IT COULD BE NULL, BUT DON'T NEED TO CHECK HERE
            return true;
        }
        int size = categories.size();
        for (int i = 0; i < size; i++) {
            Category outerCategory = categories.get(i);
            for (int j = 0; j < size; j++) {
                Category innerCategory = categories.get(j);
                if (i == j) {
                    continue;   // OK, DON'T COMPARE TO SELF
                }
                while (innerCategory != null) {
                    if (innerCategory.getId() == outerCategory.getId()) {
                        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                            .addConstraintViolation();
                        context.disableDefaultConstraintViolation();
                        return false;
                    }
                    innerCategory = innerCategory.getParent();
                }
            }
        }
        return true;
    }
    
}
