package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SendACKsToCamunda {

    public static void main (String[] args) throws Exception {
        URL url = new URL("http://localhost:3000/items/");
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

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonArray = mapper.readTree(sb.toString());
        for (JsonNode jsonNode : jsonArray) {
            String idFieldNode = jsonNode.get("id").asText();
            String loopCounterFieldNode = jsonNode.get("loopCounter").asText();
            System.out.println("id is "+idFieldNode);
            System.out.println("loopCounter is "+loopCounterFieldNode);

            URL messageUrl = new URL("http://localhost:8080/engine-rest/message");
            HttpURLConnection messageCon = (HttpURLConnection) messageUrl.openConnection();
            messageCon.setRequestMethod("POST");
            messageCon.setRequestProperty("Content-Type", "application/json");
            messageCon.setDoOutput(true);

            String input = "{\"messageName\": \"WaitForACK_"+idFieldNode+"\" }";
            System.out.println("input is "+ input);

            try(OutputStream os = messageCon.getOutputStream()) {
                byte[] byteArray = input.getBytes("utf-8");
                os.write(byteArray, 0, byteArray.length);
            }

            try(BufferedReader messageBr = new BufferedReader(
                    new InputStreamReader(messageCon.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = messageBr.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
            messageCon.disconnect();
            Thread.sleep(200);
            url = new URL("http://localhost:3000/items/"+idFieldNode);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            //con.getResponseCode();

            System.out.println("Delete response code "+ con.getResponseCode());

        }
    }
}


