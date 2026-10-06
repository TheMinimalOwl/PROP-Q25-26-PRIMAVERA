package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Test suite para la clase FreeQuestion - Versión JUnit 4
 */
public class FreeQuestionTest {

    private FreeQuestion question;
    
    @Before
    public void setUp() {
        question = new FreeQuestion();
    }
    
    @Test
    public void testDefaultConstructor() {
        FreeQuestion question = new FreeQuestion();

        assertEquals("El ID debe ser igual a UNASSIGNED_ID",
                Question.UNASSIGNED_ID.intValue(),
                question.getIdQuestion());
        assertEquals("", question.getStatement());
        assertFalse(question.isOptional());
        assertNull(question.getMaxLength());
    }
    
    @Test
    public void testParameterizedConstructorBasic() {
        question = new FreeQuestion(1, "Test statement", false);
        
        assertEquals(1, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertFalse(question.isOptional());
        assertNull(question.getMaxLength());
    }
    
    @Test
    public void testParameterizedConstructorWithMaxLength() {
        question = new FreeQuestion(2, "Test with length", true, 100);
        
        assertEquals(2, question.getIdQuestion());
        assertEquals("Test with length", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(Integer.valueOf(100), question.getMaxLength());
    }
    
    @Test
    public void testSettersAndGetters() {
        question.setIdQuestion(10);
        question.setStatement("New statement");
        question.setOptional(true);
        question.setMaxLength(200);
        
        assertEquals(10, question.getIdQuestion());
        assertEquals("New statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(Integer.valueOf(200), question.getMaxLength());
    }
    
    @Test
    public void testSetMaxLengthWithNull() {
        question.setMaxLength(100);
        assertEquals(Integer.valueOf(100), question.getMaxLength());
        
        question.setMaxLength(null);
        assertNull(question.getMaxLength());
    }
    
    @Test
    public void testHasLengthLimit() {
        assertFalse(question.hasLengthLimit());
        
        question.setMaxLength(100);
        assertTrue(question.hasLengthLimit());
        
        question.setMaxLength(null);
        assertFalse(question.hasLengthLimit());
    }
    
    @Test
    public void testToString() {
        question = new FreeQuestion(5, "Test question", true, 150);
        String result = question.toString();
        
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("statement='Test question'"));
        assertTrue(result.contains("optional=true"));
        assertTrue(result.contains("maxLength=150"));
        assertTrue(result.contains("FreeQuestion"));
    }
    
    @Test
    public void testToStringWithoutMaxLength() {
        question = new FreeQuestion(5, "Test question", false);
        String result = question.toString();
        
        assertTrue(result.contains("maxLength=unlimited"));
    }
    
    @Test
    public void testEqualsMethod() {
        FreeQuestion q1 = new FreeQuestion(1, "Statement 1", false, 100);
        FreeQuestion q2 = new FreeQuestion(1, "Statement 2", true, 200);
        FreeQuestion q3 = new FreeQuestion(2, "Statement 1", false, 100);
        
        assertTrue(q1.equals(q2)); // Mismo ID
        assertFalse(q1.equals(q3)); // Diferente ID
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullStatement() {
        new FreeQuestion(1, null, false);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetStatementWithNull() {
        question.setStatement(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetStatementWithEmpty() {
        question.setStatement("   ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxLengthZero() {
        question.setMaxLength(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxLengthNegative() {
        question.setMaxLength(-5);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxLengthTooLarge() {
        question.setMaxLength(10001);
    }
    
    @Test
    public void testSetMaxLengthValidBoundaries() {
        question.setMaxLength(1); // Mínimo válido
        assertEquals(Integer.valueOf(1), question.getMaxLength());
        
        question.setMaxLength(10000); // Máximo válido
        assertEquals(Integer.valueOf(10000), question.getMaxLength());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeIdQuestion() {
        question.setIdQuestion(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeIdForm() {
        question.setIdForm(-1);
    }
}