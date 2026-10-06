package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;


/**
 * Test suite para la clase NumericalQuestion - Versión JUnit 4
 */
public class NumericalQuestionTest {

    private NumericalQuestion question;
    
    @Before
    public void setUp() {
        question = new NumericalQuestion();
    }
    
    @Test
    public void testDefaultConstructor() {
        assertEquals(-1, question.getIdQuestion());
        assertEquals("", question.getStatement());
        assertFalse(question.isOptional());
        assertNull(question.getMinValue());
        assertNull(question.getMaxValue());
    }
    
    @Test
    public void testParameterizedConstructorBasic() {
        question = new NumericalQuestion(1, "Test statement", false);
        
        assertEquals(1, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertFalse(question.isOptional());
        assertNull(question.getMinValue());
        assertNull(question.getMaxValue());
    }
    
    @Test
    public void testParameterizedConstructorWithRange() {
        question = new NumericalQuestion(2, "Test with range", true, 0, 100);
        
        assertEquals(2, question.getIdQuestion());
        assertEquals("Test with range", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(Integer.valueOf(0), question.getMinValue());
        assertEquals(Integer.valueOf(100), question.getMaxValue());
    }
    
    @Test
    public void testSettersAndGetters() {
        question.setIdQuestion(10);
        question.setStatement("New statement");
        question.setOptional(true);
        question.setMinValue(5);
        question.setMaxValue(15);
        
        assertEquals(10, question.getIdQuestion());
        assertEquals("New statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(Integer.valueOf(5), question.getMinValue());
        assertEquals(Integer.valueOf(15), question.getMaxValue());
    }
    
    @Test
    public void testSetMinMaxWithNull() {
        question.setMinValue(1);
        question.setMaxValue(10);
        
        question.setMinValue(null);
        question.setMaxValue(null);
        
        assertNull(question.getMinValue());
        assertNull(question.getMaxValue());
    }
    
    @Test
    public void testHasRange() {
        assertFalse(question.hasRange());
        
        question.setMinValue(1);
        assertFalse(question.hasRange()); // Solo min
        
        question.setMaxValue(10);
        assertTrue(question.hasRange()); // Ambos
        
        question.setMinValue(null);
        assertFalse(question.hasRange()); // Solo max
    }
    
    @Test
    public void testGetRangeSize() {
        assertEquals(-1, question.getRangeSize()); // Sin rango
        
        question.setMinValue(1);
        assertEquals(-1, question.getRangeSize()); // Solo min
        
        question.setMaxValue(10);
        assertEquals(10, question.getRangeSize()); // 10 - 1 + 1 = 10
        
        question.setMinValue(0);
        question.setMaxValue(0);
        assertEquals(1, question.getRangeSize()); // 0 - 0 + 1 = 1
        
        question.setMinValue(-5);
        question.setMaxValue(5);
        assertEquals(11, question.getRangeSize()); // 5 - (-5) + 1 = 11
    }
    
    @Test
    public void testToString() {
        question = new NumericalQuestion(5, "Test question", false, 1, 10);
        String result = question.toString();
        
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("statement='Test question'"));
        assertTrue(result.contains("optional=false"));
        assertTrue(result.contains("range=[1 to 10]"));
        assertTrue(result.contains("NumericalQuestion"));
    }
    
    @Test
    public void testToStringWithoutRange() {
        question = new NumericalQuestion(5, "Test question", true);
        String result = question.toString();
        
        assertTrue(result.contains("range=[no min to no max]"));
    }
    
    @Test
    public void testEqualsMethod() {
        NumericalQuestion q1 = new NumericalQuestion(1, "Statement 1", false, 0, 10);
        NumericalQuestion q2 = new NumericalQuestion(1, "Statement 2", true, 5, 15);
        NumericalQuestion q3 = new NumericalQuestion(2, "Statement 1", false, 0, 10);
        
        assertTrue(q1.equals(q2)); // Mismo ID
        assertFalse(q1.equals(q3)); // Diferente ID
    }
    
    @Test
    public void testNegativeRange() {
        question.setMinValue(-100);
        question.setMaxValue(-1);
        
        assertTrue(question.hasRange());
        assertEquals(100, question.getRangeSize()); // -1 - (-100) + 1 = 100
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMinValueGreaterThanMaxValue() {
        question.setMaxValue(10);
        question.setMinValue(15); // 15 > 10
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxValueLessThanMinValue() {
        question.setMinValue(10);
        question.setMaxValue(5); // 5 < 10
    }
    
    @Test
    public void testSetValueRangeValid() {
        question.setValueRange(5, 10);
        assertEquals(Integer.valueOf(5), question.getMinValue());
        assertEquals(Integer.valueOf(10), question.getMaxValue());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetValueRangeInvalid() {
        question.setValueRange(15, 10); // 15 > 10
    }
    
    @Test
    public void testSetValueRangeWithNulls() {
        question.setValueRange(null, 10);
        assertNull(question.getMinValue());
        assertEquals(Integer.valueOf(10), question.getMaxValue());
        
        question.setValueRange(5, null);
        assertEquals(Integer.valueOf(5), question.getMinValue());
        assertNull(question.getMaxValue());
        
        question.setValueRange(null, null);
        assertNull(question.getMinValue());
        assertNull(question.getMaxValue());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNullStatement() {
        question.setStatement(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyStatement() {
        question.setStatement("   ");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeId() {
        question.setIdQuestion(-1);
    }
}