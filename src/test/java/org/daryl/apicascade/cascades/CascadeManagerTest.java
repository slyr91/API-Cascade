package org.daryl.apicascade.cascades;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import static org.junit.jupiter.api.Assertions.*;

class CascadeManagerTest {

    @Test
    void createCascadeTestWithNormalInput() {
        String name = "Test";
        Map<String, String> userInput = new HashMap<>();
        userInput.put("userParameters", "serial, asset, delete");
        userInput.put("apiURL0", "http://example.com/api/v1/devices?serial={$serial}&asset={$asset}");
        userInput.put("apiURL0Parameters", "serial=serial,asset=asset");
        userInput.put("apiURL1", "http://example.com/api/v1/devices?serial={$serial}&asset={$asset}&action={$delete}");
        userInput.put("apiURL1Parameters", "serial=serial,asset=asset,delete=delete");

        Cascade testCascade = null;
        try {
            testCascade = CascadeManager.createCascade(userInput, name);
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        if(testCascade == null) {
            fail("Failed to create a Cascade object");
        }

        Cascade trueCascade = new Cascade();

        trueCascade.setName(name);

        Parameter[] parameters = new Parameter[] {new Parameter("serial"), new Parameter("asset"), new Parameter("delete")};
        trueCascade.setParameters(new ArrayList<>(List.of(parameters)));

        APIEndpoint[] apiEndpoints = new APIEndpoint[] {new APIEndpoint("http://example.com/api/v1/devices?serial={$serial}&asset={$asset}",
                new ArrayList<>(List.of(new ParameterMapping[] {new ParameterMapping("serial", "serial"),
                new ParameterMapping("asset", "asset")}))),
                new APIEndpoint("http://example.com/api/v1/devices?serial={$serial}&asset={$asset}&action={$delete}",
                new ArrayList<>(List.of(new ParameterMapping[] {new ParameterMapping("serial", "serial"),
                        new ParameterMapping("asset", "asset"), new ParameterMapping("delete", "delete")})))};
        trueCascade.setApiEndpoints(new ArrayList<>(List.of(apiEndpoints)));

        assertEquals(trueCascade.toString(), testCascade.toString());

        File testCascadeFile = Path.of("./Cascades/Test.yaml").toFile();
        testCascadeFile.delete();
    }
}