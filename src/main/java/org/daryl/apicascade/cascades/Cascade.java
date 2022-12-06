package org.daryl.apicascade.cascades;

import java.util.List;
import java.util.stream.Collectors;

public class Cascade {
        private String name;
        private List<Parameter> parameters;
        private List<APIEndpoint> apiEndpoints;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public List<Parameter> getParameters() {
                return parameters;
        }

        public void setParameters(List<Parameter> parameters) {
                this.parameters = parameters;
        }

        public List<APIEndpoint> getApiEndpoints() {
                return apiEndpoints;
        }

        public void setApiEndpoints(List<APIEndpoint> apiEndpoints) {
                this.apiEndpoints = apiEndpoints;
        }

        @Override
        public String toString() {
                return "Cascade{" +
                        "name='" + name + '\'' +
                        ", parameters=" + ((parameters != null)?parameters.stream().map(Object::toString).collect(Collectors.joining(",")):"null") +
                        ", apiEndpoints=" + ((apiEndpoints != null)?apiEndpoints.stream().map(Object::toString).collect(Collectors.joining(",")):"null") +
                        '}';
        }
}
