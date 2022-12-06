package org.daryl.apicascade.cascades;

import okhttp3.*;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.awt.Desktop;

public class CascadeManager {

    public static final File CASCADE_FOLDER = Paths.get("Cascades").toFile();

    //TODO add input validation and request for api authorization information
    public static Cascade createCascade(String name) throws FileAlreadyExistsException {

        // Check to see if the Cascades folder has already been created and if not create it.

        if(!CASCADE_FOLDER.exists()) {
            if(!CASCADE_FOLDER.mkdir()){
                throw new RuntimeException("Unable to create Cascades folder. Please check your permissions.");
            }
        }
        if(!CASCADE_FOLDER.isDirectory()) {
            throw new RuntimeException("File exists with required folder name \"Cascades\"");
        }

        // Check to see if a cascade already exists with the given name.
        File cascadeFile = Path.of(CASCADE_FOLDER.getAbsolutePath(), name + ".yaml").toFile();
        if(cascadeFile.exists()) {
            throw new FileAlreadyExistsException("There already exists a cascade with the name " + name);
        }

        // Start the creation of a new cascade.
        Cascade cascade = new Cascade();
        cascade.setName(name);

        // Collect information from the end user.
        Scanner reader = new Scanner(System.in);
        System.out.println("Please provide a comma separated list of parameters that will be referenced in this cascade.");
        System.out.print("Parameters: ");
        String userParameters = reader.nextLine();

        List<Parameter> userParametersList = new ArrayList<>();
        String[] splitParameters = userParameters.split(",");
        for (String parameter :
                splitParameters) {
            userParametersList.add(new Parameter(parameter));
        }

        System.out.println("Next we are going to specify the API Endpoints this cascade will trigger. Please use the " +
                "following format to specify where the cascade parameters will be used. You can repeat parameters.\n" +
                "\nURL: http://example.com/api/v1/{$Parameter1}?var={$Parameter2}");

        List<APIEndpoint> endpoints = new ArrayList<>();
        System.out.println("Please provide an API endpoint for this cascade or type DONE to finish creating cascade.");
        String apiURL = reader.nextLine();

        while(!apiURL.equalsIgnoreCase("done")) {
            //TODO validate user entered URL
            List<String> urlParameters = extractParameters(apiURL);

            // Vars for the Options class
            String requestType;
            List<Header> headers = new ArrayList<>();
            String mediaType = null;
            String responseBody = null;
            List<String> rbodyParameters = new ArrayList<>();

            List<String> HTTPmethods = Arrays.asList("GET", "POST", "DELETE", "PUT");
            System.out.println("This API call uses which HTTP Method?: GET, POST, DELETE or PUT");
            requestType = reader.nextLine().toUpperCase();
            while (!HTTPmethods.contains(requestType)) {
                System.out.println("Please enter a valid HTTP Method.");
                requestType = reader.nextLine().toUpperCase();
            }

            System.out.println("Does the API Endpoint require authorization or HTTP Header information?");
            System.out.println("Example: Authorization: bearer <api-key>");
            System.out.println("y/N");
            String headersNeeded = reader.nextLine().toUpperCase();
            if(headersNeeded.equals("Y")) {
                System.out.println("Provide each header name and value. Type DONE when finished.");

                String headerName = "";
                String headerValue = "";
                while(!headerName.equalsIgnoreCase("DONE")) {
                    System.out.print("Header Name: ");
                    headerName = reader.nextLine();
                    if(headerName.equalsIgnoreCase("DONE")) {
                        break;
                    }
                    System.out.print("Header Value: ");
                    headerValue = reader.nextLine();
                    headers.add(new Header(headerName, headerValue));
                }
            } else {
                headers = null;
            }

            if(requestType.equals("POST") || requestType.equals("PUT")) {
                System.out.println("Since you are going to be sending data in the body of the HTTP request we are " +
                        "going to have to specify the format. Currently only JSON is supported.");

                mediaType = "JSON";

                System.out.println("Type out the response body in the JSON format.");
                System.out.println("Example {\"asset\": {$AssetParameter},\"serial\": {$SerialParameter}");
                System.out.print("Response Body: ");
                responseBody = reader.nextLine();
                rbodyParameters = extractParameters(responseBody);
            }

            // Map the URL parameters to the cascade parameters.
            List<ParameterMapping> parameterMappings = new ArrayList<>();
            if(!urlParameters.isEmpty() || !rbodyParameters.isEmpty()) {
                Set<String> parameterSet = new HashSet<>();
                parameterSet.addAll(urlParameters);
                parameterSet.addAll(rbodyParameters);

                System.out.println("You have the following cascade parameters available: " + userParametersList);
                System.out.println("Please provide mappings for the following Parameters to your cascade's parameters:");
                for (String urlParameter:
                        parameterSet) {
                    System.out.print(urlParameter + "= ");
                    String mappedParameter = reader.nextLine();
                    parameterMappings.add(new ParameterMapping(urlParameter, mappedParameter));
                }

            }

            Options options;
            if(requestType.equals("POST") || requestType.equals("PUT")) {
                options = new Options(requestType, headers, mediaType, responseBody);
            } else {
                options = new Options(requestType, headers);
            }

            endpoints.add(new APIEndpoint(apiURL, parameterMappings, options));

            System.out.println("Please provide an API endpoint for this cascade or type DONE to finish creating cascade.");
            apiURL = reader.nextLine();
        }

        cascade.setParameters(userParametersList);
        cascade.setApiEndpoints(endpoints);

        Yaml yaml = new Yaml();
        try (FileWriter fileWriter = new FileWriter(cascadeFile)){
            yaml.dump(cascade, fileWriter);
        } catch (IOException e) {
            System.err.println("Something went wrong while saving the new cascade file.");
            throw new RuntimeException(e);
        }

        return cascade;
    }

    private static List<String> extractParameters(String apiURL) {
        List<String> extractedParameters = new ArrayList<>();

        // Iterate through each character of the url.
        for(int i = 0; i < apiURL.length(); i++) {

            // Character matches the special variable entry character.
            if(apiURL.charAt(i) == '{') {
                // The following character also matches with the following entry character.
                if(i + 1 < apiURL.length() && apiURL.charAt(i + 1) == '$') {
                    // Iterate the pointer twice to get to the variable we want.
                    StringBuilder parameter = new StringBuilder();
                    i += 2;

                    // Append characters until the end character is reached.
                    while(i + 1 < apiURL.length() && apiURL.charAt(i) != '}') {
                        parameter.append(apiURL.charAt(i));
                        i++;
                    }
                    extractedParameters.add(parameter.toString());
                }
            }
        }
        return extractedParameters;
    }

    public static boolean deleteCascade(String name) {
        File cascadeFile = Path.of("./Cascades/" + name + ".yaml").toFile();

        if(cascadeFile.exists()) {
            return cascadeFile.delete();
        } else {
            System.out.println("Cascade with the name " + name + " does not exist.");
        }

        return false;
    }

    //TODO implement edit cascade method
    public static boolean editCascade(String name) {
        // Provide options to edit the cascade.
        boolean result = true;

        try {
            File cascadeFile = Path.of(CASCADE_FOLDER.getAbsolutePath(), name + ".yaml").toFile();

            if(!CASCADE_FOLDER.exists()) {
                throw new IllegalStateException("Cascade folder does not exist or is not a directory. Try to create a " +
                        "new cascade first.");
            } else if (!cascadeFile.exists()) {
                throw new IllegalArgumentException("No cascade with that name exists.");
            }

            Desktop.getDesktop().open(cascadeFile);

        } catch (IllegalStateException | IllegalArgumentException e) {
            result = false;
        } catch (IOException e) {
            System.err.println("Cascade file failed to open");
            result = false;
        }

        return result;
    }

    //TODO implement load cascade method
    public static Cascade loadCascade(String name) throws FileNotFoundException {
        File cascadeFile = Path.of(CASCADE_FOLDER.getAbsolutePath(), name + ".yaml").toFile();
        if(!cascadeFile.exists()) {
            throw new FileNotFoundException("No cascade with that name exists.");
        }

        Yaml yaml = new Yaml();
        FileReader freader = new FileReader(cascadeFile);
        return yaml.load(freader);
    }

    public static boolean runCascade(String name) {
        boolean result = true;
        Cascade cascade = null;

        try {
            cascade = loadCascade(name);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            result = false;
            System.exit(1);
        }

        if(cascade != null) {
            Scanner reader = new Scanner(System.in);
            System.out.println();
            System.out.println("Running the " + name + " cascade...");

            Map<String, String> parameterMappings = new HashMap<>();
            System.out.println("Please provide values for the following parameters:");
            for (Parameter parameter: cascade.getParameters()) {
                System.out.print(parameter.getName() + "= ");
                parameterMappings.put(parameter.getName(), reader.nextLine());
            }
            System.out.println();
            System.out.println();

            System.out.println("The cascade will now run with the provided parameters. API replies will be shown.");
            System.out.println();
            System.out.println();

            for(APIEndpoint endpoint: cascade.getApiEndpoints()) {
                String url = endpoint.getUrl();
                for(String parameter: parameterMappings.keySet()) {
                    url = url.replaceAll(("\\{\\$" + parameter + "\\}"), parameterMappings.get(parameter));
                }
                System.out.println("Targeted Endpoint: " + url);
                System.out.println();

                Options options = endpoint.getOptions();

                OkHttpClient client = new OkHttpClient();
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(url);

                if(Arrays.asList("GET", "DELETE").contains(options.getRequestType())) {
                    requestBuilder.get();
                } else {
                    MediaType mediaType = null;
                    if(options.getMediaType() == "JSON") {
                        mediaType = MediaType.parse("application/json");
                    } else {
                        mediaType = MediaType.parse("application/json");
                    }

                    String requestBody = options.getResponseBody();
                    for(String parameter: parameterMappings.keySet()) {
                        requestBody = requestBody.replaceAll(("\\{\\$" + parameter + "\\}"), parameterMappings.get(parameter));
                    }

//                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    RequestBody body = RequestBody.create(requestBody, mediaType);
                    requestBuilder.post(body);
                }

                // Add headers if they exist in the config file
                if(options.getHeaders() != null) {
                    for(Header header: options.getHeaders()) {
                        if(header != null) {
                            requestBuilder.addHeader(header.getName(), header.getValue());
                        }
                    }
                }

                Request request = requestBuilder.build();
                try (Response response = client.newCall(request).execute()) {
                    System.out.println("Response Code = " + response.code());
                    System.out.println("Response Message = " + response.message());
                    System.out.println(response.body().string());
                    System.out.println();
                } catch (IOException e) {
                    System.err.println("There was an issue with this API Endpoint.");
                } catch (NullPointerException e) {
                    System.err.println("Response body was null.");
                }
            }

            System.out.println(name + " Cascade run finished.");
        } else {
            result = false;
        }

        return result;
    }
}
