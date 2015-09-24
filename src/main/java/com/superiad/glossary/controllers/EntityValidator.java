package com.superiad.glossary.controllers;

import com.superiad.glossary.validation.AllGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Executes validation groups in order.  Unlike a sequence, executes
 * subsequent groups even if a prior group failed.  However, if multiple
 * groups fail for the same property, only the first match is returned.
 * @author Aaron
 */
public class EntityValidator {
    
    private static final List<Class> primaryConstraints = new ArrayList<>();
    private Validator validator;
    private Object entity;
    private Map<String,Boolean> seenFailures = new HashMap<>();
    
    static {
        primaryConstraints.addAll(Arrays.asList(AllGroup.class));
    }
    
    public EntityValidator(Validator validator, Object entity) {
        this.validator = validator;
        this.entity = entity;
    }
    
    public Set<ConstraintViolation<Object>> validateConstraints(Class... constraints) {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (Class constraint : constraints) {
            addViolations(violations,constraint);
        }
        return violations;
    }
    
    public Set<ConstraintViolation<Object>> validate() {
        Set<ConstraintViolation<Object>> violations = new HashSet<>();
        for (Class constraint : primaryConstraints) {
            addViolations(violations,constraint);
        }
        return violations;
    }
    
    private void addViolations(Set<ConstraintViolation<Object>> violations, Class constraint) {
        Set<ConstraintViolation<Object>> inner = validator.validate(entity,constraint);
        for (ConstraintViolation<Object> failure : inner) {
            String property = failure.getPropertyPath().toString();
            if (seenFailures.containsKey(property)) {
                continue;
            }
            seenFailures.put(property,true);
            violations.add(failure);
        }
    }
    
}
