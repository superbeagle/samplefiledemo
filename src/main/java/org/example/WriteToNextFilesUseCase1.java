package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

/**
 * This is an easy adapter implementation
 * illustrating how a Java Delegate can be used
 * from within a BPMN 2.0 Service Task.
 */
@Component("writeToNextFiles")
public class WriteToNextFilesUseCase1 implements JavaDelegate {

    private final Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    (String) execution.getVariable("fileName")));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                String[] splits = line.split(",");
                String completePath = (String) execution.getVariable("fileName");
                String[] pathSplits = completePath.split("\\\\");
                Path path = Paths.get(pathSplits[0]+"\\"+ pathSplits[1]+"\\"+pathSplits[2]+"\\"+pathSplits[3]+"\\"+"\\output\\"+"sample_output_"+splits[1]+".txt");
                if(Files.exists(path)){
                    line += "\r\n";
                    Files.write(path, line.getBytes(), StandardOpenOption.APPEND);
                } else {
                    line += "\r\n";
                    Files.write(path, line.getBytes(), StandardOpenOption.CREATE);
                }

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
