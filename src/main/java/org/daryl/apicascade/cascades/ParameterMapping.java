package org.daryl.apicascade.cascades;

public class ParameterMapping {
    private String apiParameter;
    private String cascadeParameter;

    public ParameterMapping(String apiParameter, String cascadeParameter) {
        this.apiParameter = apiParameter;
        this.cascadeParameter = cascadeParameter;
    }

    public String getApiParameter() {
        return apiParameter;
    }

    public void setApiParameter(String apiParameter) {
        this.apiParameter = apiParameter;
    }

    public String getCascadeParameter() {
        return cascadeParameter;
    }

    public void setCascadeParameter(String cascadeParameter) {
        this.cascadeParameter = cascadeParameter;
    }

    @Override
    public String toString() {
        return "ParameterMapping{" +
                "apiParameter='" + apiParameter + '\'' +
                ", cascadeParameter='" + cascadeParameter + '\'' +
                '}';
    }
}
