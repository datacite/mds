package org.datacite.mds.validation.constraints.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    Logger log = Logger.getLogger(UniqueValidator.class);

    @PersistenceContext
    transient EntityManager entityManager;

    String field;
    String idField;
    String defaultMessage;

    public void initialize(Unique constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.idField = constraintAnnotation.idField();
        this.defaultMessage = constraintAnnotation.message();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean isValid(Object entity, ConstraintValidatorContext context) {
        if (entityManager == null) {
            log.warn("entityManager not injected");
            return true;
        }
        
        Serializable id, value;
        try {
            id = (Serializable) PropertyUtils.getProperty(entity, idField);
            value = (Serializable) PropertyUtils.getProperty(entity, field);
        } catch (Exception e) {
            log.debug("error getting property:" + e.getMessage());
            return false;
        }
        log.debug("entity=" + entity.getClass().getName() + ", id: " + idField + "=" + id + ", field: " + field + "=" + value);

        String qstr = "SELECT " + idField + " FROM " + entity.getClass().getName() + " WHERE " + field + " = :value";
        TypedQuery<Serializable> q = entityManager.createQuery(qstr, Serializable.class);
        q.setParameter("value", value);
        List<Serializable> results = q.getResultList();

        Boolean isUnique = true;
        if (results.size() == 1) {
            Serializable foundId = results.get(0);
            log.debug("#results=1 (" + idField + "=" + foundId + ")");
            isUnique = foundId.equals(id);
        } else {
            log.debug("#results=" + results.size());
            isUnique = results.size() == 0;
        }
        log.debug("isUnique=" + isUnique);
        
        if (!isUnique) {
          ValidationUtils.addConstraintViolation(context, defaultMessage, field);
        }
        
        return isUnique;
    }

}
