package com.bill.lottor.models;

import com.bill.lottor.models.displays.ACompany;
import com.bill.lottor.rester.Rester;

public class MyAppl {
    public MyCompany getMyCompany(String companyId) {
        return new MyCompany("");
    }

    public ACompany[] listCompany() {
        Rester.getInstance().listCompany();
        return null;
    }


}
