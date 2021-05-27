package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component("getFileAndStoreData")
public class GetFileAndStoreDataUseCase2 implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    (String) execution.getVariable("fileName")));
            String line = reader.readLine();
            execution.setVariable("malformed", false);
            URL url = new URL("http://localhost:3000/secondUseCaseSeparateDataStoreStaging");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            while (line != null) {
                System.out.println(line);

                if(line.startsWith("error")) {
                    execution.setVariable("malformed", true);
                }

                String[] splits = line.split(",");


                String input = "{\"id\": \""+splits[0]+"\", \"data\": \""+splits[1]+"\"}";
                System.out.println("input is "+ input);

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
                Thread.sleep(200);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                // read next line
                line = reader.readLine();

            }
            con.disconnect();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
