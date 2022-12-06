package org.daryl.apicascade.cascades;

public class Parameter {
    private String name;

    public Parameter(String name) {
        this.name = name;
    }

    public Parameter() {};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + ((name != null)?name:"null") + '\'' +
                '}';
    }
}
