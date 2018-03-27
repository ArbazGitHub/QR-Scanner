package com.tech.qrcodeexample.model;

import java.io.Serializable;

/**
 * Created by arbaz on 5/8/17.
 */

public class GenerateQrObject implements Serializable {
    String fullName;
    String id;

    public GenerateQrObject(String fullName, String id) {
        this.fullName = fullName;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
