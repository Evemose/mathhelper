package org.mathhelper.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mathhelper.model.equation.CreateEquationDTO;
import org.mathhelper.model.equation.Equation;
import org.mathhelper.repositories.EquationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EquationsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String COMMON_PREFIX = "/equations";

    @Test
    public void testGetAllEquations() {
        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

//    @Test
//    public void testGetEquation() {
//        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX+"/1", String.class);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//    }

    @Test
    public void testGetEquation_InvalidId() {
        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX+"/-1", String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testCreateEquation() {
        var createEquationDTO = new CreateEquationDTO("3-2*x/5=4");
        var responseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        var responseBody = restTemplate.getForObject(responseEntity.getHeaders().getLocation(), Equation.class);
        assertNotNull(responseBody);
        assertEquals("3-2*x/5=4", responseBody.getEquationString());
        assertThat(responseBody.getPolynomialOfEquation().getNumeratorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(1, -2d, 0, -5d));
        assertThat(responseBody.getPolynomialOfEquation().getDenominatorCoefficients())
                .containsExactlyInAnyOrderEntriesOf(Map.of(0, 5d));
    }

    @Test
    @DirtiesContext
    public void testCreateEquation_InvalidEquation() {
        var createEquationDTO = new CreateEquationDTO("3-2*x/5)=4");
        var responseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String[].class);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).containsExactlyInAnyOrder(
                "Parentheses are not balanced: 3-2*x/5)",
                        "Left side of equation is invalid: 3-2*x/5)"
                );
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_ValidSolution() {
        var createEquationDTO = new CreateEquationDTO("4*x/5=4");
        var equationResponseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String.class);
        assertEquals(HttpStatus.CREATED, equationResponseEntity.getStatusCode());
        var responseEntity =
                restTemplate.postForEntity(equationResponseEntity.getHeaders().getLocation()+"/solutions?x=5",
                        null, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_InvalidSolution() {
        var createEquationDTO = new CreateEquationDTO("4*x/5=4");
        var equationResponseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String.class);
        assertEquals(HttpStatus.CREATED, equationResponseEntity.getStatusCode());
        var responseEntity =
                restTemplate.postForEntity(equationResponseEntity.getHeaders().getLocation()+"/solutions?x=6",
                        null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Solution does not fit the equation.", responseEntity.getBody());
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_InvalidEquation() {
        var responseEntity =
                restTemplate.postForEntity(COMMON_PREFIX+"/-1/solutions?x=6",
                        null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}