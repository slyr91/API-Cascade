package org.daryl.apicascade.cascades;

import java.util.List;

public class Options {

    private String requestType;
    private List<Header> headers;
    private String mediaType;
    private String responseBody;

    public Options(String requestType, List<Header> headers) {
        this(requestType, headers, null, null);
    }

    public Options(String requestType, List<Header> headers, String mediaType, String responseBody) {
        this.requestType = requestType;
        this.headers = headers;
        this.mediaType = mediaType;
        this.responseBody = responseBody;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }
}
