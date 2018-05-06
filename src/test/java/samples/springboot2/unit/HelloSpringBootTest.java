package samples.springboot2.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import samples.springboot2.config.ApplicationConfiguration;
import samples.springboot2.controllers.HelloSpringBoot;
import samples.springboot2.services.UserService;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ HelloSpringBoot.class, ApplicationConfiguration.class, UserService.class})
@AutoConfigureMockMvc
public class HelloSpringBootTest {
    @Autowired private MockMvc mvc;
    @Autowired private MappingJackson2JsonView jsonView;

    @Test
    public void getHelloSpringBoot() throws Exception {
        ObjectMapper mapper = jsonView.getObjectMapper();
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Map message = mapper.readValue(response.getContentAsByteArray(), Map.class);
        assert(message.get("Message").toString().equals("Hello Spring Boot 2"));
    }
}
