package org.datacite.mds.web.ui.model;

import org.datacite.mds.validation.constraints.Symbol;

public class ChangePasswordMailModel {
    @Symbol(value = {Symbol.Type.ALLOCATOR, Symbol.Type.DATACENTRE}, hasToExist=true)
    String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
