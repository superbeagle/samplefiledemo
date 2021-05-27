package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Scanner;

public class MonitorForNewFileUseCase1 {
    public static void main (String[] args) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("C:/tmp/TexasCapitalBank/UseCase1");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            boolean done = false;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("file name: "+ event.context());
                    try {
                        URL url = new URL("http://localhost:8080/engine-rest/process-definition/key/UseCase1/start");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                        File file = new File(path.toString() + "/" + event.context());

                        // create an object of Scanner
                        // associated with the file
                        Scanner sc = new Scanner(file);

                        int lines = 0;
                        // read each line and
                        // count number of lines
                        while(sc.hasNextLine()) {
                            sc.nextLine();
                            lines++;
                        }
                        System.out.println("Total Number of Lines: " + path.toString().replace("\\","\\\\") + "\\\\"+ event.context());

                        String payload = "{\n" +
                                "  \"variables\": {\n" +
                                "    \"fileName\" : {\n" +
                                "        \"value\" : \"" + path.toString().replace("\\","\\\\") + "\\\\"+ event.context() + "\",\n" +
                                "        \"type\": \"String\"\n" +
                                "    },\n" +
                                "    \"fileLength\" : {\n" +
                                "      \"value\" : \""+lines+"\",\n" +
                                "      \"type\": \"Integer\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}";
                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                        writer.write(payload);
                        writer.close();

                        if (conn.getResponseCode() != 200) {
                            throw new RuntimeException("Failed : HTTP error code : "
                                    + conn.getResponseCode());
                        }

                        conn.disconnect();

                    } catch (MalformedURLException e) {

                        e.printStackTrace();

                    } catch(IOException e) {

                        e.printStackTrace();

                    }


                }

                key.reset();
            }

        } catch (java.io.IOException ioe) {
            System.out.println("IO error "+ioe);
        } catch (java.lang.InterruptedException e) {
            System.out.println("Interruption error "+ e);
        }
    }
}