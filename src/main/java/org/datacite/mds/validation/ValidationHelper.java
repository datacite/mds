package org.datacite.mds.validation;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationHelper {

    @Autowired
    Validator validator;

    /**
     * Method to get the error message from the first violation thrown by the
     * validator on the given object
     * 
     * @param object
     *            object to be validated
     * @return String containing the first error message of null if the object
     *         is valid
     */
    public <T> String getViolationMessages(T object) {
        Collection<String> messages = new ArrayList<String>();
        for (ConstraintViolation<T> violation : validator.validate(object)) {
            messages.add("[" + violation.getPropertyPath() + "] " + violation.getMessage());
        }

        String messagesJoined = StringUtils.join(messages, "; ");
        return StringUtils.defaultIfEmpty(messagesJoined, null);
    }

    public void validate(Object object) throws ValidationException {
        String violationMessage = getViolationMessages(object);
        if (violationMessage != null)
            throw new ValidationException(violationMessage);
    }

}
