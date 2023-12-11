package org.mathhelper.services;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mathhelper.model.Equation;
import org.mathhelper.repositories.EquationRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class EquationServiceTest {

	@InjectMocks
	private EquationService equationService;
	@Mock
	private EquationRepository equationRepository;

	private AutoCloseable closeable;

	@BeforeEach
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		equationService = new EquationService(equationRepository);
	}

	@AfterEach
	public void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	void checkForEqualXTest() {
		var equation = Equation.builder().equation("2*x-4=0").build();
		assertTrue(equationService.checkForX(equation, 2.0));
	}

	@Test
	void checkForNotEqualXTest() {
		var equation = Equation.builder().equation("2*x-4=0").build();
		assertFalse(equationService.checkForX(equation, 3.0));
	}
}