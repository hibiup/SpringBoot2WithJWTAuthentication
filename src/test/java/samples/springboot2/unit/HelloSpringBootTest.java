package samples.springboot2.unit;

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
import samples.springboot2.services.JWTAuthenticationService;
import samples.springboot2.services.JWTService;
import samples.springboot2.services.UserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={
        HelloSpringBoot.class,
        ApplicationConfiguration.class,
        UserService.class,
        JWTService.class,
        JWTAuthenticationService.class})
@AutoConfigureMockMvc
public class HelloSpringBootTest {
    @Autowired private MockMvc mvc;
    @Autowired private MappingJackson2JsonView jsonView;

    @Test
    public void getHelloSpringBoot() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        Map message = jsonView.getObjectMapper().readValue(response.getContentAsByteArray(), Map.class);
        assert(message.get("Message").toString().equals("Hello Spring Boot 2"));
    }

    @Test
    public void testGetToken() throws Exception {
        assert(requestToken() != null);
    }

    @Test
    public void testRequestUserApi() throws Exception {
        String token = requestToken();
        MockHttpServletResponse response = requestWithToken(token);
        Map message = jsonView.getObjectMapper().readValue(response.getContentAsByteArray(), Map.class);
        assert(message.get("username").toString().equals("mockuser"));
        assert(message.get("password").toString().equals(token));
    }

    protected String requestToken() throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/api/token")
                .with(httpBasic("mockuser","mockpass")))
                .andExpect(status().isOk()).andReturn().getResponse();
        return response.getContentAsString();
    }

    protected MockHttpServletResponse requestWithToken(String token) throws Exception {
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get("/api/user")
                .header("Authorization", "JWT " + token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse();
        return response;
    }
}
