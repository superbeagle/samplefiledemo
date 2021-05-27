package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component("moveData")
public class MoveDataUseCase2 implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {
        URL url = new URL("http://localhost:3000/secondUseCaseSeparateDataStoreStaging");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("accept", "application/json");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String l = null;
        while ((l=br.readLine())!=null) {
            sb.append(l);
        }
        br.close();
        con.disconnect();

        url = new URL("http://localhost:3000/secondUseCaseSeparateDataStoreProper");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        String input = sb.toString();

        try(OutputStream os = con.getOutputStream()) {
            byte[] byteArray = input.getBytes("utf-8");
            os.write(byteArray, 0, byteArray.length);
        }
        try(BufferedReader obr = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = obr.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        con.disconnect();
    }
}
