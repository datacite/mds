package org.datacite.mds.validation.constraints.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    Logger log = Logger.getLogger(UniqueValidator.class);

    @PersistenceContext
    transient EntityManager entityManager;

    String[] uniqueFields;
    String idField;
    String defaultMessage;

    public void initialize(Unique constraintAnnotation) {
        this.uniqueFields = constraintAnnotation.field();
        this.idField = constraintAnnotation.idField();
        this.defaultMessage = constraintAnnotation.message();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean isValid(Object entity, ConstraintValidatorContext context) {
        Object idFieldValue = getProperty(entity, idField);
        Object uniqueFieldsValue[] = new Object[uniqueFields.length];
        
        String logMessage = "entity: " + entity.getClass().getName() + ", id: " + idField + "=" + idFieldValue;
        for (int i = 0; i < uniqueFields.length; i++) {
            uniqueFieldsValue[i] = getProperty(entity, uniqueFields[i]);
            logMessage += ", field: " + uniqueFields[i] + "=" + uniqueFieldsValue[i];
        }
        log.trace(logMessage);

        List<Serializable> foundIds = findIds(entity, uniqueFieldsValue);

        Boolean isUnique = foundIds.size() == 0 || foundIds.contains(idFieldValue);
        log.trace("isUnique=" + isUnique);

        for (String field : uniqueFields)
            ValidationUtils.addConstraintViolation(context, defaultMessage, field);

        return isUnique;
    }

    private Object getProperty(Object entity, String field) {
        try {
            return PropertyUtils.getProperty(entity, field);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Serializable> findIds(Object entity, Object[] values) {
        String queryString = "SELECT " + idField + " FROM " + entity.getClass().getName();
        for (int i = 0; i < uniqueFields.length; i++) {
            queryString +=  (i==0 ? " WHERE " : " AND ") + uniqueFields[i]  + " = ?";
        }
        TypedQuery<Serializable> query = entityManager.createQuery(queryString, Serializable.class);
        for (int i = 0; i < uniqueFields.length; i++) {
            int parameterPosition = i + 1; //ordinal parameters are 1-based! 
            query.setParameter(parameterPosition, values[i]);
        }
        List<Serializable> foundIds = query.getResultList();
        log.trace("found IDs " + foundIds);
        return foundIds;
    }

}
