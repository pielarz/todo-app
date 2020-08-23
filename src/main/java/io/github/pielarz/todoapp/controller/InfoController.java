package io.github.pielarz.todoapp.controller;

import io.github.pielarz.todoapp.config.TaskConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is info controller, to get information from application properties file.
 */

@RestController
public class InfoController {

    private DataSourceProperties datasource;
    private TaskConfigurationProperties myProp;

    public InfoController(DataSourceProperties datasource, TaskConfigurationProperties myProp) {
        this.datasource = datasource;
        this.myProp = myProp;
    }

    @GetMapping("/info/url")
    String url(){
        return datasource.getUrl();
    }

    @GetMapping("info/prop")
    boolean myProp(){
        return myProp.getTemplate().isAllowMultipleTasks();
    }
}
