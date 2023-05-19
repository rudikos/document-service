package com.workwolf.documentservice;

import com.workwolf.documentservice.property.DocumentStorageProperties;
import gate.Gate;
import gate.creole.Plugin;
import gate.util.GateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableConfigurationProperties({
        DocumentStorageProperties.class
})
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setGateProperties() throws GateException, IOException {
        String directory = System.getProperty("user.dir");
        System.setProperty("gate.site.config", directory + "/GATEFiles/gate.xml");
        Gate.setGateHome(new File(directory + "/GATEFiles"));
        Gate.setPluginsHome(new File(directory + "/GATEFiles/plugins"));
        Gate.init();
    }
}
