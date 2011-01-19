package org.datacite.mds.validation.constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Unique(field = "uniqField")
@Entity
public class UniqueTestEntity {
    @Id
    private Integer id;

    private String uniqField;

    public static UniqueTestEntity create(Integer id, String uniqField) {
        UniqueTestEntity entity = new UniqueTestEntity();
        entity.id = id;
        entity.uniqField = uniqField;
        return entity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqField() {
        return uniqField;
    }

    public void setUniqField(String uniqField) {
        this.uniqField = uniqField;
    }
}
