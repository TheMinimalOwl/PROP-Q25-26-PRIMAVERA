package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Test suite para la clase AnswerFreeQuestion - Versión JUnit 4
 */
public class AnswerFreeQuestionTest {

    private AnswerFreeQuestion answer;
    
    @Before
    public void setUp() {
        answer = new AnswerFreeQuestion();
    }
    
    @Test
    public void testDefaultConstructor() {
        assertEquals(0, answer.getIdUser());
        assertEquals(0, answer.getIdForm());
        assertEquals(0, answer.getIdQuestion());
        assertEquals("", answer.getAnswerText());
        assertTrue(answer.isEmpty());
    }
    
    @Test
    public void testParameterizedConstructor() {
        answer = new AnswerFreeQuestion(100, 1, 5, "Test answer");
        
        assertEquals(100, answer.getIdUser());
        assertEquals(1, answer.getIdForm());
        assertEquals(5, answer.getIdQuestion());
        assertEquals("Test answer", answer.getAnswerText());
        assertFalse(answer.isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParameterizedConstructorWithNull() {
        new AnswerFreeQuestion(101, 1, 5, null);
    }
    
    @Test
    public void testSettersAndGetters() {
        answer.setIdUser(200);
        answer.setIdForm(5);
        answer.setIdQuestion(10);
        answer.setAnswerText("New text");
        
        assertEquals(200, answer.getIdUser());
        assertEquals(5, answer.getIdForm());
        assertEquals(10, answer.getIdQuestion());
        assertEquals("New text", answer.getAnswerText());
        assertEquals(8, answer.getLength());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetAnswerTextWithNull() {
        answer.setAnswerText(null);
    }
    
    @Test
    public void testIsEmptyWithEmptyString() {
        assertTrue(answer.isEmpty());
        
        answer.setAnswerText("");
        assertTrue(answer.isEmpty());
        
        answer.setAnswerText("   ");
        assertTrue(answer.isEmpty());
        
        answer.setAnswerText("Valid text");
        assertFalse(answer.isEmpty());
    }
    
    @Test
    public void testGetLength() {
        assertEquals(0, answer.getLength());
        
        answer.setAnswerText("Hello");
        assertEquals(5, answer.getLength());
        
        answer.setAnswerText("");
        assertEquals(0, answer.getLength());
        
        answer.setAnswerText("  Spaces  ");
        assertEquals(10, answer.getLength());
    }
    
    @Test
    public void testToString() {
        answer = new AnswerFreeQuestion(150, 2, 3, "Sample answer");
        String result = answer.toString();
        
        assertTrue(result.contains("AnswerFreeQuestion"));
        assertTrue(result.contains("idUser=150"));
        assertTrue(result.contains("idForm=2"));
        assertTrue(result.contains("idQuestion=3"));
        assertTrue(result.contains("Sample answer"));
    }

    @Test
    public void testEqualsMethod() {
        AnswerFreeQuestion a1 = new AnswerFreeQuestion(1, 10, 5, "Text");
        AnswerFreeQuestion a2 = new AnswerFreeQuestion(1, 10, 5, "Different Text"); // Mismo idUser, idForm, idQuestion
        AnswerFreeQuestion a3 = new AnswerFreeQuestion(2, 10, 5, "Text"); // Diferente idUser

        assertTrue(a1.equals(a2)); // Mismo idUser, idForm, idQuestion
        assertFalse(a1.equals(a3)); // Diferente idUser
    }

    @Test
    public void testConstructorWithZeroIds() {
        answer = new AnswerFreeQuestion(0, 0, 0, "Test");
        
        assertEquals(0, answer.getIdUser());
        assertEquals(0, answer.getIdForm());
        assertEquals(0, answer.getIdQuestion());
        assertEquals("Test", answer.getAnswerText());
        assertFalse(answer.isEmpty());
    }
    
    @Test
    public void testLongText() {
        String longText = "This is a very long text that might be used as an answer to a free question";
        answer.setAnswerText(longText);
        
        assertEquals(longText.length(), answer.getLength());
        assertEquals(longText, answer.getAnswerText());
        assertFalse(answer.isEmpty());
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
    
// MODIFICACIÓN: AnswerFreeQuestionTest.java - solo el test problemático
@Test
public void testIsValidForQuestion() {
    FreeQuestion question = new FreeQuestion(1, "Test question", true, 100);
    
    // Respuesta vacía en pregunta opcional - DEBERÍA SER VÁLIDA
    answer.setAnswerText("");
    assertTrue(answer.isValidForQuestion(question));
    
    // Respuesta con solo espacios en pregunta opcional - DEBERÍA SER VÁLIDA
    answer.setAnswerText("   ");
    assertTrue(answer.isValidForQuestion(question));
    
    // Respuesta válida
    answer.setAnswerText("Valid answer");
    assertTrue(answer.isValidForQuestion(question));
    
    // Respuesta que excede longitud máxima
    String longText = "This is a very long answer that exceeds the maximum length allowed for this question. " +
                     "This text should be longer than 100 characters to properly test the length validation.";
    answer.setAnswerText(longText);
    assertFalse("Answer should be invalid when exceeding max length", answer.isValidForQuestion(question));
    
    // Respuesta vacía en pregunta obligatoria - DEBERÍA SER INVÁLIDA
    FreeQuestion requiredQuestion = new FreeQuestion(2, "Required question", false, 50);
    answer.setAnswerText("");
    assertFalse("Empty answer should be invalid for required question", answer.isValidForQuestion(requiredQuestion));
    
    // Respuesta con solo espacios en pregunta obligatoria - DEBERÍA SER INVÁLIDA
    answer.setAnswerText("   ");
    assertFalse("Whitespace-only answer should be invalid for required question", 
                answer.isValidForQuestion(requiredQuestion));
    
    // Respuesta válida en pregunta obligatoria
    answer.setAnswerText("Valid answer for required question");
    assertTrue(answer.isValidForQuestion(requiredQuestion));
}
    
    @Test(expected = IllegalArgumentException.class)
    public void testIsValidForQuestionWithNull() {
        answer.isValidForQuestion(null);
    }
}