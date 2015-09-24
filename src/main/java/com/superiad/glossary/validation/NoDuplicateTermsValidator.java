package com.superiad.glossary.validation;

import com.superiad.glossary.model.Term;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.LabelInvalidException;
import com.superiad.glossary.persistence.LabelNotUniqueException;
import com.superiad.glossary.persistence.Repository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mwhitrock
 */
@Service
public class NoDuplicateTermsValidator implements ConstraintValidator<NoDuplicateTerms, Term> {
    
    private NoDuplicateTerms constraintAnnotation;
    @Autowired
    private Repository termRepositoryImpl;

    @Override
    public void initialize(NoDuplicateTerms constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Term term, ConstraintValidatorContext context) {
        if (term.getName() == null) {
            return true; // CHECKED ELSEWHERE
        }
        if (term.getId() != null) {
            return true; // ONLY CHECK ON CREATION ...
        }
        Term possibleMatch = null;
        try {
            possibleMatch = (Term) termRepositoryImpl.findByLabel(term.getName());
        }
        catch(EntityNotFoundException | LabelInvalidException e) {
            return true;
        }
        catch(LabelNotUniqueException e) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addConstraintViolation();
            context.disableDefaultConstraintViolation();
            return false;
        }
        if (possibleMatch == null || possibleMatch.getId().equals(term.getId())) {
            return true;
        }
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addConstraintViolation();
        context.disableDefaultConstraintViolation();
        return false;
    }
    
}
