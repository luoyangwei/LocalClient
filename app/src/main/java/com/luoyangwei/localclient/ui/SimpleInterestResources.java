package com.luoyangwei.localclient.ui;

import com.luoyangwei.localclient.data.model.Resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SimpleInterestResources {
    private static SimpleInterestResources instance;
    private List<Resource> resources = new ArrayList<>();

    public static SimpleInterestResources getInstance() {
        if (instance == null) {
            instance = new SimpleInterestResources();
        }
        return instance;
    }

}
