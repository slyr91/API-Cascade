package org.daryl.apicascade.cascades;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.*;
import java.awt.Desktop;

public class CascadeManager {

    public static final File CASCADE_FOLDER = Path.of("./Cascades").toFile();

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

        while(!apiURL.toLowerCase().equals("done")) {
            //TODO validate user entered URL
            List<String> urlParameters = extractURLParameters(apiURL);

            //TODO add requests for tokens needed to authenticate with API Endpoint

            // Map the URL parameters to the cascade parameters.
            List<ParameterMapping> parameterMappings = new ArrayList<>();
            if(!urlParameters.isEmpty()) {
                Set<String> urlParameterSet = new HashSet<>(urlParameters);
                System.out.println("You have the following cascade parameters available: " + userParametersList.toString());
                System.out.println("Please provide mappings for the following URL Parameters to your cascade's parameters:");
                for (String urlParameter:
                        urlParameterSet) {
                    System.out.print(urlParameter + "= ");
                    String mappedParameter = reader.nextLine();
                    parameterMappings.add(new ParameterMapping(urlParameter, mappedParameter));
                }

            }

            endpoints.add(new APIEndpoint(apiURL, parameterMappings));

            System.out.println("Please provide the an API endpoint for this cascade or type DONE to finish creating cascade.");
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

    private static List<String> extractURLParameters(String apiURL) {
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
    public static Cascade loadCascade(String name) {
        return new Cascade();
    }
}
