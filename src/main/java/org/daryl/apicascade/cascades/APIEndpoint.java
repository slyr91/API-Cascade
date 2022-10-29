package org.daryl.apicascade.cascades;

import java.util.List;
import java.util.stream.Collectors;

public class APIEndpoint {
    private String url;
    private List<ParameterMapping> mappings;

    public APIEndpoint(String url, List<ParameterMapping> mappings) {
        this.url = url;
        this.mappings = mappings;
    }

    public List<ParameterMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<ParameterMapping> mappings) {
        this.mappings = mappings;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "APIEndpoint{" +
                "url='" + url + '\'' +
                ", mappings=" + mappings.stream().map(Object::toString).collect(Collectors.joining(",")) +
                '}';
    }
}
