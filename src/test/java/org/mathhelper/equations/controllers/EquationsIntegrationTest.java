package org.mathhelper.equations.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mathhelper.equations.dtos.CreateEquationDTO;
import org.mathhelper.equations.dtos.GetEquationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EquationsIntegrationTest {

    private final String COMMON_PREFIX = "/equations";
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAllEquations() {
        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX, Void.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetEquation() {
        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX + "/1", GetEquationDTO.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("4*x-5=3", responseEntity.getBody().equation());
        // TODO add solutions validation when implement automatic solution adding
    }

    @Test
    public void testGetEquation_InvalidId() {
        var responseEntity = restTemplate.getForEntity(COMMON_PREFIX + "/-1", Void.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testCreateEquation() {
        var createEquationDTO = new CreateEquationDTO("3-2*x/5=4");

        var responseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        var body = responseEntity.getBody();
        assertNotNull(body);
        // assertEquals("3-2*x/5=4", body.equation());
        // TODO add solutions validation when implement automatic solution adding
    }

    @Test
    @DirtiesContext
    public void testCreateEquation_InvalidEquation() {
        var createEquationDTO = new CreateEquationDTO("3-2*x/5)=4");
        var responseEntity = restTemplate.postForEntity(COMMON_PREFIX, createEquationDTO, String[].class);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).containsExactlyInAnyOrder(
                "Parentheses are not balanced: 3-2*x/5)");
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_ValidSolution() {
        var responseEntity =
                restTemplate.postForEntity(COMMON_PREFIX + "/3/solutions?x=1.5",
                        null, String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_InvalidSolution() {
        var responseEntity =
                restTemplate.postForEntity(COMMON_PREFIX + "/1/solutions?x=6",
                        null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Solution does not fit the equation.", responseEntity.getBody());
    }

    @Test
    @DirtiesContext
    public void testAddSolutionIfFits_InvalidEquation() {
        var responseEntity =
                restTemplate.postForEntity(COMMON_PREFIX + "/-1/solutions?x=6",
                        null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}