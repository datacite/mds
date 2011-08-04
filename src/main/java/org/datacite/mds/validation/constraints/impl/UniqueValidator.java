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

    String uniqueField;
    String idField;
    String defaultMessage;

    public void initialize(Unique constraintAnnotation) {
        this.uniqueField = constraintAnnotation.field();
        this.idField = constraintAnnotation.idField();
        this.defaultMessage = constraintAnnotation.message();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean isValid(Object entity, ConstraintValidatorContext context) {
        Serializable idFieldValue = getProperty(entity, idField);
        Serializable uniqueFieldValue = getProperty(entity, uniqueField);
        log.trace("entity: " + entity.getClass().getName() + ", id: " + idField + "=" + idFieldValue + ", field: "
                + uniqueField + "=" + uniqueFieldValue);

        List<Serializable> foundIds = findIds(entity, uniqueFieldValue);

        Boolean isUnique = foundIds.size() == 0 || foundIds.contains(idFieldValue);
        log.trace("isUnique=" + isUnique);

        ValidationUtils.addConstraintViolation(context, defaultMessage, uniqueField);

        return isUnique;
    }

    private Serializable getProperty(Object entity, String field) {
        try {
            return (Serializable) PropertyUtils.getProperty(entity, field);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Serializable> findIds(Object entity, Serializable value) {
        String queryString = "SELECT " + idField + " FROM " + entity.getClass().getName() + " WHERE " + uniqueField
                + " = :value";
        TypedQuery<Serializable> query = entityManager.createQuery(queryString, Serializable.class);
        query.setParameter("value", value);
        List<Serializable> foundIds = query.getResultList();
        log.trace("found IDs with '" + uniqueField + "=" + value + "': " + foundIds);
        return foundIds;
    }

}
