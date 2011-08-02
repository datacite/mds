package org.datacite.mds.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple tool to create datasets. Datacentre symbol has to be specified on
 * command line. DOIs are read from stdin, one DOI per line
 */
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DatasetCreator extends AbstractTool {
    
    private static Logger log = Logger.getLogger(DatasetCreator.class);

    @Override
    public void run(String[] args) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("exactly one parameter (datacentre symbol) has to be supplied");
        String symbol = args[0];

        Datacentre datacentre = Datacentre.findDatacentreBySymbol(symbol);
        if (datacentre == null)
            throw new RuntimeException("cannot find datacentre '" + symbol + "'");

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(isr);

        String line = reader.readLine();
        while (line != null) {
            if (StringUtils.isNotEmpty(line))
                addDataset(datacentre, line);
            line = reader.readLine();
        }
    }

    public void addDataset(Datacentre datacentre, String doi) {
        System.out.print(doi + ": ");
        try {
            persistDataset(datacentre, doi);
            System.out.println("OK");
            log.info(datacentre.getSymbol() + " succesfully created " + doi);
        } catch (ConstraintViolationException ex) {
            String msg = ValidationUtils.collateViolationMessages(ex.getConstraintViolations());
            System.out.println(msg);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void persistDataset(Datacentre datacentre, String doi) {
        Dataset dataset = new Dataset();
        dataset.setDoi(doi);
        dataset.setDatacentre(datacentre);
        dataset.persist();
    }

    public static void main(String[] args) {
        initAndRun(args);
    }

}
