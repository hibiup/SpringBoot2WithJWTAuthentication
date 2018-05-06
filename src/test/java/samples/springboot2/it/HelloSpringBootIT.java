package samples.springboot2.it;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import samples.springboot2.Main;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class,
        webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloSpringBootIT {
    @LocalServerPort private int port;
    @Autowired private TestRestTemplate template;

    final static String URL_BASE = "http://localhost";

    @Test
    public void getHelloSpringBoot() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/xml");

        ResponseEntity<Map> response = template.exchange(
                URL_BASE + ":" + port,
                HttpMethod.GET,
                new HttpEntity(headers),
                Map.class);
        assertThat(response.getBody().get("Message").toString(), equalTo("Hello Spring Boot 2"));
    }

    @Test
    public void testGetToken() {
        assert(requestToken() != null);
    }

    @Test
    public void testGetUserWithToken() {
        String token = requestToken();
        ResponseEntity<Map> response = getUserWithToken(token);
        assert(response.getBody().get("username").toString().equals("mockuser"));
        assert(response.getBody().get("password").toString().equals(token));
    }

    protected String requestToken() {
        ResponseEntity<String> response = template
                .withBasicAuth("mockuser", "mockpass")
                .getForEntity(URL_BASE + ":" + port + "/api/token", String.class);
        return response.getBody().toString();
    }

    protected ResponseEntity<Map> getUserWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "JWT " + token);

        ResponseEntity<Map> response = template
                .exchange(URL_BASE + ":" + port + "/api/user",
                        HttpMethod.GET,
                        new HttpEntity(headers),
                        Map.class);
        return response;
    }
}
