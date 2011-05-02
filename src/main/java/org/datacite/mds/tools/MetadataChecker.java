package org.datacite.mds.tools;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.service.SchemaService;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MetadataChecker extends AbstractTool {

    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    SchemaService schemaService;
    
    @Autowired
    Validator validator;
    
    @Override
    @Transactional
    public void run(String[] args) {
        List<Dataset> datasets = Dataset.findAllDatasets();
        for (Dataset dataset : datasets) {
            Metadata metadata = Metadata.findLatestMetadatasByDataset(dataset);
            checkMetadata(metadata);
        }
    }
    
    private void checkMetadata(Metadata metadata) {
        if (metadata == null) 
            return;
        
        String doi = metadata.getDataset().getDoi();
        byte[] xml = metadata.getXml();

        String schemaLocation = schemaService.getSchemaLocation(xml);
        Set violations = validator.validate(metadata);
        
        String violationMsg = ValidationUtils.collateViolationMessages(violations);
        System.out.println(doi + "\t" + schemaLocation + "\t" + violationMsg);
    }
    
    public static void main(String[] args) throws Exception {
        initAndRun(args);
    }

}
