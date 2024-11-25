package com.luoyangwei.localclient.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Bucket
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Bucket {
    private String id;
    private String name;

    /**
     * 最近的资源 ID
     */
    private String mostRecentResourceId;

    /**
     * 资源数量
     */
    private int count;

    /**
     * 资源列表
     */
    private List<Resource> resources;
}
