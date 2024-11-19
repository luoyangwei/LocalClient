package com.luoyangwei.localclient.ui.photo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransitionNameChangedEvent {
    private String name;
    private String resourceId;
}
