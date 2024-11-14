package com.luoyangwei.localclient.data.source.local;

import com.luoyangwei.localclient.data.model.ImageResource;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResourceEntry {
    private String id;
    private String title;
    private String data;
    private ImageResource resource;
}