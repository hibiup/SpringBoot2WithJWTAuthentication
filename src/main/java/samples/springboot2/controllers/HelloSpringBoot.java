package samples.springboot2.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloSpringBoot {
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {
                MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE
    })
    @ResponseBody
    Map<String, Object> hello() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("Message", "Hello Spring Boot 2");
        return model;
    }
}
