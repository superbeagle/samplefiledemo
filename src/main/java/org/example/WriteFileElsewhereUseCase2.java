package org.example;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("writeFileElsewhere")
public class WriteFileElsewhereUseCase2 implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {

    }
}
