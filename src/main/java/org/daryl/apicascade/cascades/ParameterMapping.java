package org.daryl.apicascade.cascades;

public class ParameterMapping {
    private String apiParamter;
    private String cascadeParamter;

    public ParameterMapping(String apiParameter, String cascadeParameter) {
        this.apiParamter = apiParameter;
        this.cascadeParamter = cascadeParameter;
    }

    public String getApiParamter() {
        return apiParamter;
    }

    public void setApiParamter(String apiParamter) {
        this.apiParamter = apiParamter;
    }

    public String getCascadeParamter() {
        return cascadeParamter;
    }

    public void setCascadeParamter(String cascadeParamter) {
        this.cascadeParamter = cascadeParamter;
    }
}
