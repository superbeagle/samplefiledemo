package org.example;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component("sendDataToQueue")
public class SendDataToQueueUseCase1 implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {
        // Check malformed flag
        if((boolean) execution.getVariable("malformed")) {
            throw new BpmnError("MalformedFile", "The file "+execution.getVariable("fileName")+" is malformed.");
        }

        // Write 'id' and loopCounter to 'database'
        URL url = new URL("http://localhost:3000/items/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String input = "{\"id\": \"" + execution.getVariable("piid") + "\", \"loopCounter\": \"" + execution.getVariable("loopCounter") + "\" }";

        try(OutputStream os = con.getOutputStream()) {
            byte[] byteArray = input.getBytes("utf-8");
            os.write(byteArray, 0, byteArray.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        con.disconnect();

//        System.out.println("Loop counter is "+ execution.getVariable("loopCounter"));
//        if((int) execution.getVariable("loopCounter") == 3 ) {
//            throw new BpmnError("MalformedFile", "The file "+execution.getVariable("fileName")+" is malformed.");
//        }
    }
}
