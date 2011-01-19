package org.datacite.mds.validation.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
@Transactional
public class UniqueTest extends AbstractContraintsTest {

    @PersistenceContext
    EntityManager entityManager;

    final Integer ID_PERSISTENT = 42;
    final Integer ID_OTHER = 23;
    final String FIELD_PERSISTENT = "foo";
    final String FIELD_OTHER = "bar";

    UniqueTestEntity persistentEntity;

    @BeforeTransaction
    public void initBeforeTransaction() {
        persistentEntity = UniqueTestEntity.create(ID_PERSISTENT, FIELD_PERSISTENT);
        entityManager.persist(persistentEntity);
    }

    @Test
    public void test() {
        assertTrue(isValid(null, FIELD_OTHER));
        assertFalse(isValid(null, FIELD_PERSISTENT));
        assertTrue(isValid(ID_OTHER, FIELD_OTHER));
        assertFalse(isValid(ID_OTHER, FIELD_PERSISTENT));
        assertTrue(isValid(ID_PERSISTENT, FIELD_PERSISTENT));
        assertTrue(isValid(ID_PERSISTENT, FIELD_OTHER));
    }

    boolean isValid(Integer id, String uniqField) {
        UniqueTestEntity entity = UniqueTestEntity.create(id, uniqField);
        return getValidationHelper().isValid(entity, Unique.class);
    }
}
