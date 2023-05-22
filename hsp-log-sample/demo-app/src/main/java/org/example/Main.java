package org.example;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import java.util.ServiceConfigurationError;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Log4j2
public class Main {

    public static void main(String[] args)  {
        ThreadContext.put("id", UUID.randomUUID().toString());
        try {
            ThreadContext.put("organization", "hspsandbox");
            log.info("Sample Hello World! " + RandomGenerator.getDefault().nextInt());
            Thread.sleep(6000);
        } catch (ServiceConfigurationError er) {
            System.out.println("Service configuration error: " + er.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ThreadContext.clearAll();
        LogManager.shutdown();
    }
}