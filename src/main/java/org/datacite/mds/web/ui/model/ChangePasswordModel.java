package org.datacite.mds.web.ui.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePasswordModel {
    @NotNull
    @Size(min=8, max=30)
    String first;
    String second ;
    
    @AssertTrue(message="{org.datacite.mds.validation.other.FieldEquals.message}")
    public boolean isEqual() {
        return getFirst().equals(getSecond());
    }
    
    public String getFirst() {
        return first;
    }
    public void setFirst(String first) {
        this.first = first;
    }
    public String getSecond() {
        return second;
    }
    public void setSecond(String second) {
        this.second = second;
    }
}
