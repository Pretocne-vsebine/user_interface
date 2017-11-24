package com.rso.streaming;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestConfig {

    @ConfigValue(value = "external-dependencies.clip-service.url", watch = true)
    private String clipUrl;

    public String getClipUrl() {
        return clipUrl;
    }

    public void setClipUrl(String clipUrl) {
        this.clipUrl = clipUrl;
    }
}
