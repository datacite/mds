package org.datacite.mds.tools;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ConsistencyChecker extends AbstractTool {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    SchemaService schemaService;

    @Autowired
    Validator validator;

    @Override
    @Transactional
    public void run(String[] args) {
        checkList(Allocator.findAllAllocators());
        checkList(Datacentre.findAllDatacentres());
        checkList(Prefix.findAllPrefixes());
        checkList(Dataset.findAllDatasets());
        checkList(Metadata.findLatestMetadatas());
    }

    @SuppressWarnings("unchecked")
    private void checkList(List list) {
        if (!list.isEmpty()) {
            String clazz = list.get(0).getClass().getSimpleName();
            System.out.println("checking " + clazz);
            for (Object entity : list) {
                check(entity);
            }
        }
        System.out.println();
    }

    private void check(Object entity) {
        if (entity == null)
            return;

        try {
            Set violations = validator.validate(entity);

            if (!violations.isEmpty()) {
                String violationMsg = ValidationUtils.collateViolationMessages(violations);
                System.out.println(entity + "\t" + violationMsg);
            }
        } catch (ValidationException ex) {
            System.out.println(entity + "\t" + ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        initAndRun(args);
    }

}
