package org.datacite.mds.web.api.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.datacite.mds.web.api.ApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StatusController implements ApiController {
    
    @PersistenceContext
    EntityManager em;
    
    @RequestMapping(value = "/status")
    public ResponseEntity<String> status() {
        checkDatabaseConnection();
        return new ResponseEntity<String>("OK", null, HttpStatus.OK);
    }
    
    private void checkDatabaseConnection() {
        em.createNativeQuery("SELECT 1").getSingleResult();
    }

}
