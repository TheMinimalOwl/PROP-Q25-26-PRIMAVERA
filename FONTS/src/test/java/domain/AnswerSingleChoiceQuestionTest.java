package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite para la clase AnswerSingleChoiceQuestion - Versión JUnit 4
 */
public class AnswerSingleChoiceQuestionTest {

    private AnswerSingleChoiceQuestion answer;

    @Before
    public void setUp() {
        answer = new AnswerSingleChoiceQuestion();
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(0, answer.getIdUser());
        assertEquals(0, answer.getIdForm());
        assertEquals(0, answer.getIdQuestion());
        assertNull(answer.getSelectedOptionIndex());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        answer = new AnswerSingleChoiceQuestion(100, 3, 6, 2);

        assertEquals(100, answer.getIdUser());
        assertEquals(3, answer.getIdForm());
        assertEquals(6, answer.getIdQuestion());
        assertEquals(Integer.valueOf(2), answer.getSelectedOptionIndex());
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithNull() {
        answer = new AnswerSingleChoiceQuestion(101, 3, 6, null);

        assertEquals(101, answer.getIdUser());
        assertEquals(3, answer.getIdForm());
        assertEquals(6, answer.getIdQuestion());
        assertNull(answer.getSelectedOptionIndex());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        answer.setIdUser(200);
        answer.setIdForm(15);
        answer.setIdQuestion(20);
        answer.setSelectedOptionIndex(3);

        assertEquals(200, answer.getIdUser());
        assertEquals(15, answer.getIdForm());
        assertEquals(20, answer.getIdQuestion());
        assertEquals(Integer.valueOf(3), answer.getSelectedOptionIndex());
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testSetSelectedOptionIndexWithNull() {
        answer.setSelectedOptionIndex(5);
        assertFalse(answer.isEmpty());

        answer.setSelectedOptionIndex(null);
        assertTrue(answer.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSelectedOptionIndexWithNegative() {
        answer.setSelectedOptionIndex(-1);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(answer.isEmpty());

        answer.setSelectedOptionIndex(0);
        assertFalse(answer.isEmpty());

        answer.setSelectedOptionIndex(1);
        assertFalse(answer.isEmpty());

        answer.setSelectedOptionIndex(null);
        assertTrue(answer.isEmpty());
    }

    // MODIFICACIÓN: AnswerSingleChoiceQuestionTest.java - solo el test problemático
    @Test
    public void testIsValidIndex() {
        answer.setSelectedOptionIndex(2);

        // Casos válidos
        assertTrue(answer.isValidIndex(5)); // 2 < 5
        assertTrue(answer.isValidIndex(3)); // 2 < 3
        assertTrue(answer.isValidIndex(10)); // 2 < 10

        // Casos inválidos
        assertFalse(answer.isValidIndex(2)); // 2 >= 2
        assertFalse(answer.isValidIndex(1)); // 2 >= 1
        // Eliminamos la línea que pasaba 0 como optionCount
    }

    // Añadimos un test específico para el caso con 0 opciones
    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIndexWithZeroOptions() {
        answer.setSelectedOptionIndex(0);
        answer.isValidIndex(0); // Debería lanzar excepción porque optionCount debe ser positivo
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIndexWithZeroOptionCount() {
        answer.isValidIndex(0); // optionCount debe ser positivo
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidIndexWithNegativeOptionCount() {
        answer.isValidIndex(-5); // optionCount debe ser positivo
    }

    @Test
    public void testIsValidIndexWithNullIndex() {
        answer.setSelectedOptionIndex(null);
        assertFalse(answer.isValidIndex(5));
    }

    @Test
    public void testIsValidIndexWithNegativeIndex() {
        try {
            // Usar reflexión para establecer el valor directamente sin pasar por el setter
            java.lang.reflect.Field field = AnswerSingleChoiceQuestion.class.getDeclaredField("selectedOptionIndex");
            field.setAccessible(true);
            field.set(answer, -1);

            assertFalse(answer.isValidIndex(5));
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    @Test
    public void testIsValidIndexEdgeCases() {
        // Primer índice válido
        answer.setSelectedOptionIndex(0);
        assertTrue(answer.isValidIndex(1));
        assertTrue(answer.isValidIndex(5));

        // Último índice válido
        answer.setSelectedOptionIndex(4);
        assertTrue(answer.isValidIndex(5));
        assertFalse(answer.isValidIndex(4));
    }

    @Test
    public void testToString() {
        answer = new AnswerSingleChoiceQuestion(150, 4, 8, 1);
        String result = answer.toString();

        assertTrue(result.contains("AnswerSingleChoiceQuestion"));
        assertTrue(result.contains("idUser=150"));
        assertTrue(result.contains("idForm=4"));
        assertTrue(result.contains("idQuestion=8"));
        assertTrue(result.contains("selectedOptionIndex=1"));
    }

    @Test
    public void testEqualsMethod() {
        AnswerSingleChoiceQuestion a1 = new AnswerSingleChoiceQuestion(1, 10, 5, 0);
        AnswerSingleChoiceQuestion a2 = new AnswerSingleChoiceQuestion(1, 10, 5, 2); // Mismo idUser, idForm, idQuestion
        AnswerSingleChoiceQuestion a3 = new AnswerSingleChoiceQuestion(2, 10, 5, 0); // Diferente idUser

        assertTrue(a1.equals(a2)); // Mismo idUser, idForm, idQuestion
        assertFalse(a1.equals(a3)); // Diferente idUser
    }

    @Test
    public void testZeroIndex() {
        answer.setSelectedOptionIndex(0);
        assertFalse(answer.isEmpty());
        assertEquals(Integer.valueOf(0), answer.getSelectedOptionIndex());
        assertTrue(answer.isValidIndex(1)); // Una opción disponible
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
        OrderedSingleQuestion question = new OrderedSingleQuestion(1, "Test question", true);
        question.addOption("Option 1");
        question.addOption("Option 2");
        question.addOption("Option 3");

        // Respuesta vacía en pregunta opcional
        assertTrue(answer.isValidForQuestion(question));

        // Respuesta válida
        answer.setSelectedOptionIndex(1);
        assertTrue(answer.isValidForQuestion(question));

        // Índice inválido
        answer.setSelectedOptionIndex(5);
        assertFalse(answer.isValidForQuestion(question));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidForQuestionWithNull() {
        answer.isValidForQuestion(null);
    }
}