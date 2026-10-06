package domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suite para la clase AnswerMultipleChoiceQuestion - Versión JUnit 4
 */
public class AnswerMultipleChoiceQuestionTest {

    private AnswerMultipleChoiceQuestion answer;

    @Before
    public void setUp() {
        answer = new AnswerMultipleChoiceQuestion();
    }

    @Test
    public void testDefaultConstructor() {
        assertEquals(0, answer.getIdUser());
        assertEquals(0, answer.getIdForm());
        assertEquals(0, answer.getIdQuestion());
        assertTrue(answer.getSelectedOptionIndexes().isEmpty());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        List<Integer> indexes = Arrays.asList(0, 2, 4);
        answer = new AnswerMultipleChoiceQuestion(100, 4, 5, indexes);

        assertEquals(100, answer.getIdUser());
        assertEquals(4, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertEquals(3, answer.getSelectionCount());
        assertTrue(answer.getSelectedOptionIndexes().containsAll(indexes));
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithNull() {
        answer = new AnswerMultipleChoiceQuestion(101, 4, 5, null);

        assertEquals(101, answer.getIdUser());
        assertEquals(4, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertTrue(answer.getSelectedOptionIndexes().isEmpty());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithEmptyList() {
        answer = new AnswerMultipleChoiceQuestion(102, 4, 5, new ArrayList<>());

        assertEquals(102, answer.getIdUser());
        assertEquals(4, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertTrue(answer.getSelectedOptionIndexes().isEmpty());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        List<Integer> indexes = Arrays.asList(1, 3, 5);

        answer.setIdUser(200);
        answer.setIdForm(20);
        answer.setIdQuestion(25);
        answer.setSelectedOptionIndexes(indexes);

        assertEquals(200, answer.getIdUser());
        assertEquals(20, answer.getIdForm());
        assertEquals(25, answer.getIdQuestion());
        assertEquals(3, answer.getSelectionCount());
        assertTrue(answer.getSelectedOptionIndexes().containsAll(indexes));
        assertFalse(answer.isEmpty());
    }

    @Test
    public void testSetSelectedOptionIndexesWithNull() {
        answer.setSelectedOptionIndexes(Arrays.asList(1, 2, 3));
        assertFalse(answer.isEmpty());

        answer.setSelectedOptionIndexes(null);
        assertTrue(answer.isEmpty());
        assertTrue(answer.getSelectedOptionIndexes().isEmpty());
    }

    @Test
    public void testAddOptionIndex() {
        answer.addOptionIndex(1);
        answer.addOptionIndex(3);
        answer.addOptionIndex(1); // Duplicado, no debería agregarse

        assertEquals(2, answer.getSelectionCount());
        assertTrue(answer.getSelectedOptionIndexes().contains(1));
        assertTrue(answer.getSelectedOptionIndexes().contains(3));
        assertFalse(answer.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidOptionIndex() {
        answer.addOptionIndex(-1); // Índice negativo, debería lanzar excepción
    }

    @Test
    public void testRemoveOptionIndex() {
        answer.addOptionIndex(1);
        answer.addOptionIndex(3);
        answer.addOptionIndex(5);

        answer.removeOptionIndex(1);
        assertEquals(2, answer.getSelectionCount());
        assertFalse(answer.getSelectedOptionIndexes().contains(1));
        assertTrue(answer.getSelectedOptionIndexes().contains(3));
        assertTrue(answer.getSelectedOptionIndexes().contains(5));

        answer.removeOptionIndex(10); // Índice no existente
        assertEquals(2, answer.getSelectionCount()); // Sin cambios
    }

    @Test
    public void testRemoveAllOptionIndexes() {
        answer.addOptionIndex(0);
        answer.addOptionIndex(1);
        answer.addOptionIndex(2);

        answer.removeOptionIndex(0);
        answer.removeOptionIndex(1);
        answer.removeOptionIndex(2);

        assertTrue(answer.getSelectedOptionIndexes().isEmpty());
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testAreValidIndexes() {
        answer.setSelectedOptionIndexes(Arrays.asList(0, 2, 4));

        assertTrue(answer.areValidIndexes(5)); // Todos los índices < 5
        assertFalse(answer.areValidIndexes(4)); // 4 >= 4, inválido
        assertFalse(answer.areValidIndexes(3)); // 4 >= 3 y 2 >= 3, inválido
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAreValidIndexesWithZeroOptionCount() {
        answer.areValidIndexes(0); // optionCount debe ser positivo
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAreValidIndexesWithNegativeOptionCount() {
        answer.areValidIndexes(-5); // optionCount debe ser positivo
    }

    @Test
    public void testAreValidIndexesWithInvalidIndexes() {
        answer.setSelectedOptionIndexes(Arrays.asList(0, 10)); // 10 >= 5, inválido
        assertFalse(answer.areValidIndexes(5));
    }

    @Test
    public void testAreValidIndexesWithEmptyList() {
        assertTrue(answer.areValidIndexes(5)); // Lista vacía siempre es válida
    }

    @Test
    public void testIsInSelectionRange() {
        answer.setSelectedOptionIndexes(Arrays.asList(0, 1, 2));

        // Casos válidos
        assertTrue(answer.isInSelectionRange(1, 5)); // 3 está entre 1 y 5
        assertTrue(answer.isInSelectionRange(3, 3)); // 3 está entre 3 y 3
        assertTrue(answer.isInSelectionRange(0, 10)); // 3 está entre 0 y 10

        // Casos inválidos
        assertFalse(answer.isInSelectionRange(4, 5)); // 3 < 4, fuera de rango
        assertFalse(answer.isInSelectionRange(1, 2)); // 3 > 2, fuera de rango
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInSelectionRangeWithNegativeMin() {
        answer.isInSelectionRange(-1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInSelectionRangeWithNegativeMax() {
        answer.isInSelectionRange(1, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsInSelectionRangeWithInvalidRange() {
        answer.isInSelectionRange(5, 1); // min > max
    }

    @Test
    public void testIsInSelectionRangeWithEmptyList() {
        assertFalse(answer.isInSelectionRange(1, 3)); // 0 selecciones, mínimo es 1
        assertTrue(answer.isInSelectionRange(0, 3)); // 0 selecciones, mínimo es 0
    }

    @Test
    public void testGetSelectionCount() {
        assertEquals(0, answer.getSelectionCount());

        answer.addOptionIndex(0);
        assertEquals(1, answer.getSelectionCount());

        answer.addOptionIndex(1);
        answer.addOptionIndex(2);
        assertEquals(3, answer.getSelectionCount());

        answer.removeOptionIndex(1);
        assertEquals(2, answer.getSelectionCount());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(answer.isEmpty());

        answer.addOptionIndex(0);
        assertFalse(answer.isEmpty());

        answer.removeOptionIndex(0);
        assertTrue(answer.isEmpty());
    }

    @Test
    public void testToString() {
        answer = new AnswerMultipleChoiceQuestion(150, 4, 6, Arrays.asList(0, 2));
        String result = answer.toString();

        assertTrue(result.contains("AnswerMultipleChoiceQuestion"));
        assertTrue(result.contains("idUser=150"));
        assertTrue(result.contains("idForm=4"));
        assertTrue(result.contains("idQuestion=6"));
        assertTrue(result.contains("selectedOptionIndexes=[0, 2]"));
    }

    @Test
    public void testEqualsMethod() {
        AnswerMultipleChoiceQuestion a1 = new AnswerMultipleChoiceQuestion(1, 10, 5, Arrays.asList(0, 1));
        AnswerMultipleChoiceQuestion a2 = new AnswerMultipleChoiceQuestion(1, 10, 5, Arrays.asList(2, 3)); // Mismo idUser, idForm, idQuestion
        AnswerMultipleChoiceQuestion a3 = new AnswerMultipleChoiceQuestion(2, 10, 5, Arrays.asList(0, 1)); // Diferente idUser

        assertTrue(a1.equals(a2)); // Mismo idUser, idForm, idQuestion
        assertFalse(a1.equals(a3)); // Diferente idUser
    }

    @Test
    public void testDefensiveCopyInGetter() {
        List<Integer> originalList = new ArrayList<>(Arrays.asList(1, 2, 3));
        answer.setSelectedOptionIndexes(originalList);

        List<Integer> returnedList = answer.getSelectedOptionIndexes();
        returnedList.add(4); // No debería afectar la lista interna

        assertEquals(3, answer.getSelectionCount());
        assertFalse(answer.getSelectedOptionIndexes().contains(4));
    }

    @Test
    public void testDefensiveCopyInSetter() {
        List<Integer> originalList = new ArrayList<>(Arrays.asList(1, 2, 3));
        answer.setSelectedOptionIndexes(originalList);

        originalList.add(4); // No debería afectar la lista interna
        assertEquals(3, answer.getSelectionCount());
        assertFalse(answer.getSelectedOptionIndexes().contains(4));
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
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"));
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(1, "Test question", true, options, 1, 2);

        // Respuesta vacía en pregunta opcional
        assertTrue(answer.isValidForQuestion(question));

        // Respuesta válida
        answer.addOptionIndex(0);
        answer.addOptionIndex(1);
        assertTrue(answer.isValidForQuestion(question));

        // Demasiadas selecciones
        answer.addOptionIndex(2);
        assertFalse(answer.isValidForQuestion(question));

        // Índices inválidos
        answer.setSelectedOptionIndexes(Arrays.asList(0, 5)); // 5 no existe
        assertFalse(answer.isValidForQuestion(question));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsValidForQuestionWithNull() {
        answer.isValidForQuestion(null);
    }

@Test(expected = IllegalArgumentException.class)
public void testSetSelectedOptionIndexesWithNegativeIndex() {
    List<Integer> invalidIndexes = Arrays.asList(0, -1, 2);
    answer.setSelectedOptionIndexes(invalidIndexes);
}
}