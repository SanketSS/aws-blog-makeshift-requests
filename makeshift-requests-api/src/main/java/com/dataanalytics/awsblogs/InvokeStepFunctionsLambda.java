package com.dataanalytics.awsblogs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsAsyncClientBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class InvokeStepFunctionsLambda implements RequestStreamHandler {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        LambdaLogger logger = context.getLogger();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
        try
        {
            HashMap event = gson.fromJson(reader, HashMap.class);
            //logger.log("event data: " + gson.toJson(event));
            JsonObject contextObject = gson.fromJson(gson.toJson(event.get("context")), JsonObject.class);
            String cognitoAppClientId = contextObject.get("cognito-app-client-id").toString();
            String requestId = contextObject.get("request-id").toString();

            logger.log("cognito-app-client-id: " + cognitoAppClientId);
            logger.log("request-id: " + requestId);


            //invoke step function
            //invokeStepFunction();
            String jsonRequestString = "{\"request_id\" : \""+requestId+"\" , ";
            writer.write(gson.toJson(jsonRequestString));
            if (writer.checkError())
            {
                logger.log("WARNING: Writer encountered an error.");
            }
        }
        catch (IllegalStateException | JsonSyntaxException exception)
        {
            logger.log(exception.toString());
        }
        finally
        {
            reader.close();
            writer.close();
        }
    }
    private void invokeStepFunction() {
        AWSStepFunctionsAsyncClientBuilder.standard()
                .withClientConfiguration(new ClientConfiguration())
                .withRegion(Regions.US_EAST_1)
                .build();
    }
}
