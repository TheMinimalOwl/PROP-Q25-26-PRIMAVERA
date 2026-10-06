package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite para la clase AnswerNumericalQuestion - Versión JUnit 4
 */
public class AnswerNumericalQuestionTest {

    private AnswerNumericalQuestion answer;

    @Before
    public void setUp() {
        answer = new AnswerNumericalQuestion();
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(0, answer.getIdUser());
        assertEquals(0, answer.getIdForm());
        assertEquals(0, answer.getIdQuestion());
        assertNull(answer.getNumericalValue());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        answer = new AnswerNumericalQuestion(100, 2, 5, 42);

        assertEquals(100, answer.getIdUser());
        assertEquals(2, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertEquals(Integer.valueOf(42), answer.getNumericalValue());
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithNull() {
        answer = new AnswerNumericalQuestion(101, 2, 5, null);

        assertEquals(101, answer.getIdUser());
        assertEquals(2, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertNull(answer.getNumericalValue());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        answer.setIdUser(200);
        answer.setIdForm(10);
        answer.setIdQuestion(15);
        answer.setNumericalValue(100);

        assertEquals(200, answer.getIdUser());
        assertEquals(10, answer.getIdForm());
        assertEquals(15, answer.getIdQuestion());
        assertEquals(Integer.valueOf(100), answer.getNumericalValue());
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testSetNumericalValueWithNull() {
        answer.setNumericalValue(50);
        assertFalse(answer.isEmpty());

        answer.setNumericalValue(null);
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(answer.isEmpty());

        answer.setNumericalValue(0);
        assertFalse(answer.isEmpty());

        answer.setNumericalValue(-5);
        assertFalse(answer.isEmpty());

        answer.setNumericalValue(null);
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testIsInRange() {
        answer.setNumericalValue(5);

        // Casos válidos
        assertTrue(answer.isInRange(1, 10));
        assertTrue(answer.isInRange(5, 5));
        assertTrue(answer.isInRange(0, 5));
        assertTrue(answer.isInRange(5, 10));

        // Casos inválidos
        assertFalse(answer.isInRange(6, 10));
        assertFalse(answer.isInRange(1, 4));
        assertFalse(answer.isInRange(10, 20));
    }

    @Test
    public void testIsInRangeWithNullBounds() {
        answer.setNumericalValue(5);

        // Límites nulos (sin restricción)
        assertTrue(answer.isInRange(null, 10));
        assertTrue(answer.isInRange(1, null));
        assertTrue(answer.isInRange(null, null));

        // Valor nulo siempre retorna false
        answer.setNumericalValue(null);
        assertFalse(answer.isInRange(1, 10));
        assertFalse(answer.isInRange(null, null));
    }

    @Test
    public void testIsInRangeWithNegativeNumbers() {
        answer.setNumericalValue(-5);

        assertTrue(answer.isInRange(-10, 0));
        assertTrue(answer.isInRange(-5, -5));
        assertFalse(answer.isInRange(0, 10));
        assertFalse(answer.isInRange(-10, -6));
    }

    @Test
    public void testIsInRangeEdgeCases() {
        answer.setNumericalValue(Integer.MAX_VALUE);
        assertTrue(answer.isInRange(Integer.MAX_VALUE, Integer.MAX_VALUE));

        answer.setNumericalValue(Integer.MIN_VALUE);
        assertTrue(answer.isInRange(Integer.MIN_VALUE, Integer.MIN_VALUE));
    }

    @Test
    public void testToString() {
        answer = new AnswerNumericalQuestion(150, 3, 7, 123);
        String result = answer.toString();

        assertTrue(result.contains("AnswerNumericalQuestion"));
        assertTrue(result.contains("idUser=150"));
        assertTrue(result.contains("idForm=3"));
        assertTrue(result.contains("idQuestion=7"));
        assertTrue(result.contains("numericalValue=123"));
    }

    @Test
    public void testEqualsMethod() {
        AnswerNumericalQuestion a1 = new AnswerNumericalQuestion(1, 10, 5, 10);
        AnswerNumericalQuestion a2 = new AnswerNumericalQuestion(1, 10, 5, 20); // Mismo idUser, idForm, idQuestion
        AnswerNumericalQuestion a3 = new AnswerNumericalQuestion(2, 10, 5, 10); // Diferente idUser

        assertTrue(a1.equals(a2)); // Mismo idUser, idForm, idQuestion
        assertFalse(a1.equals(a3)); // Diferente idUser
    }

    @Test
    public void testZeroValue() {
        answer.setNumericalValue(0);
        assertFalse(answer.isEmpty());
        assertEquals(Integer.valueOf(0), answer.getNumericalValue());
    }

    // NUEVOS TESTS PARA EXCEPCIONES
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeIdUser() {
        answer.setIdUser(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeIdForm() {
        answer.setIdForm(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeIdQuestion() {
        answer.setIdQuestion(-1);
    }

    @Test
    public void testIsValidForQuestion() {
        NumericalQuestion question = new NumericalQuestion(1, "Test question", true, 0, 100);

        // Respuesta vacía en pregunta opcional
        assertTrue(answer.isValidForQuestion(question));

        // Respuesta válida
        answer.setNumericalValue(50);
        assertTrue(answer.isValidForQuestion(question));

        // Respuesta fuera de rango
        answer.setNumericalValue(150);
        assertFalse(answer.isValidForQuestion(question));

        // Respuesta vacía en pregunta obligatoria
        NumericalQuestion requiredQuestion = new NumericalQuestion(2, "Required question", false, 0, 100);
        answer.setNumericalValue(null);
        assertFalse(answer.isValidForQuestion(requiredQuestion));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidForQuestionWithNull() {
        answer.isValidForQuestion(null);
    }
}