package com.luoyangwei.localclient.data.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ResourceModel extends ViewModel {
    private static final MutableLiveData<List<Resource>> resources = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Resource>> getResources() {
        return resources;
    }

    public void add(Resource resource) {
        List<Resource> list = resources.getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(resource);
        resources.postValue(list);
    }
}
