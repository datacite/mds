package org.datacite.mds.validation.constraints.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.log4j.Logger;
import org.datacite.mds.validation.constraints.Unique;

public class UniqueValidator implements ConstraintValidator<Unique, Serializable> {

    Logger log = Logger.getLogger(UniqueValidator.class);

    @PersistenceContext
    transient EntityManager entityManager;

    Class<?> entity;
    String field;

    public void initialize(Unique constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
        this.field = constraintAnnotation.field();
    }

    public boolean isValid(Serializable value, ConstraintValidatorContext context) {
        // if hibernate persists the object the entityManager is not injected,
        // so we always return true.
        if (entityManager == null)
            return true;
        log.debug("entity=" + entity.getName() + ", field=" + field + ", value=" + value);
        String qstr = "SELECT count(*) FROM " + entity.getName() + " WHERE " + field + " = :value";
        TypedQuery<Number> q = entityManager.createQuery(qstr, Number.class);
        q.setParameter("value", value);
        Boolean isUnique = q.getSingleResult().longValue() == 0;
        log.debug("isUnique: " + isUnique);
        return isUnique;
    }

}
