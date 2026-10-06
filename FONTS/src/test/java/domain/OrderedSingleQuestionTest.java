package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test suite para la clase OrderedSingleQuestion - Versión JUnit 4
 */
public class OrderedSingleQuestionTest {

    private OrderedSingleQuestion question;
    
    @Before
    public void setUp() {
        question = new OrderedSingleQuestion();
    }
    
    @Test
    public void testDefaultConstructor() {
        assertEquals(-1, question.getIdQuestion());
        assertEquals("", question.getStatement());
        assertFalse(question.isOptional());
        assertTrue(question.getOptions().isEmpty());
    }
    
    @Test
    public void testParameterizedConstructorBasic() {
        question = new OrderedSingleQuestion(1, "Test statement", false);
        
        assertEquals(1, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertFalse(question.isOptional());

        assertTrue(question.getOptions().isEmpty());
    }
    
    @Test
    public void testParameterizedConstructorWithOptions() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("First", "Second", "Third"));
        question = new OrderedSingleQuestion(2, "Test statement", true, options);
        
        assertEquals(2, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(3, question.getOptionCount());
        assertEquals("First", question.getOptions().get(0));
        assertEquals("Second", question.getOptions().get(1));
        assertEquals("Third", question.getOptions().get(2));
    }
    
    @Test
    public void testSettersAndGetters() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Option 1", "Option 2"));
        
        question.setIdQuestion(10);
        question.setStatement("New statement");
        question.setOptional(true);
        question.setOptions(options);
        
        assertEquals(10, question.getIdQuestion());
        assertEquals("New statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(2, question.getOptionCount());
    }
    
    @Test
    public void testSetOptionsWithNull() {
        question.setOptions(null);
        assertTrue(question.getOptions().isEmpty());
        assertEquals(0, question.getOptionCount());
    }
    
    @Test
    public void testAddOption() {
        question.addOption("First option");
        question.addOption("Second option");
        
        assertEquals(2, question.getOptionCount());
        assertTrue(question.getOptions().contains("First option"));
        assertTrue(question.getOptions().contains("Second option"));
    }
    
    @Test
    public void testAddOptionTrimsSpaces() {
        question.addOption("  Option with spaces  ");
        assertEquals("Option with spaces", question.getOptions().get(0));
    }
    
    @Test
    public void testRemoveOption() {
        question.addOption("Option A");
        question.addOption("Option B");
        question.addOption("Option C");
        
        question.removeOption(1); // Remover "Option B"
        
        assertEquals(2, question.getOptionCount());
        assertEquals("Option A", question.getOptions().get(0));
        assertEquals("Option C", question.getOptions().get(1));
    }
    
    @Test
    public void testGetOptionCount() {
        assertEquals(0, question.getOptionCount());
        
        question.addOption("Option 1");
        assertEquals(1, question.getOptionCount());
        
        question.addOption("Option 2");
        question.addOption("Option 3");
        assertEquals(3, question.getOptionCount());
        
        question.removeOption(1);
        assertEquals(2, question.getOptionCount());
    }
    
    @Test
    public void testDefensiveCopyInGetter() {
        ArrayList<String> originalOptions = new ArrayList<>(Arrays.asList("A", "B", "C"));
        question.setOptions(originalOptions);
        
        ArrayList<String> returnedOptions = question.getOptions();
        returnedOptions.add("D"); // No debería afectar la lista interna
        
        assertEquals(3, question.getOptionCount());
        assertFalse(question.getOptions().contains("D"));
    }
    
    @Test
    public void testDefensiveCopyInSetter() {
        ArrayList<String> originalOptions = new ArrayList<>(Arrays.asList("A", "B", "C"));
        question.setOptions(originalOptions);
        
        originalOptions.add("D"); // No debería afectar la lista interna
        assertEquals(3, question.getOptionCount());
        assertFalse(question.getOptions().contains("D"));
    }
    
    @Test
    public void testToString() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Option 1", "Option 2"));
        question = new OrderedSingleQuestion(5, "Test question", false, options);
        String result = question.toString();
        
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("statement='Test question'"));
        assertTrue(result.contains("optional=false"));
        assertTrue(result.contains("options=2"));
        assertTrue(result.contains("type=ordered-single"));
        assertTrue(result.contains("OrderedSingleQuestion"));
    }
    
    @Test
    public void testEqualsMethod() {
        OrderedSingleQuestion q1 = new OrderedSingleQuestion(1, "Q1", false);
        OrderedSingleQuestion q2 = new OrderedSingleQuestion(1, "Q2", true);
        OrderedSingleQuestion q3 = new OrderedSingleQuestion(2, "Q1", false);
        
        assertTrue(q1.equals(q2)); // Mismo ID
        assertFalse(q1.equals(q3)); // Diferente ID
    }
    
    @Test
    public void testConstructorWithEmptyOptions() {
        ArrayList<String> emptyOptions = new ArrayList<>();
        question = new OrderedSingleQuestion(1, "Test", false, emptyOptions);
        
        assertEquals(0, question.getOptionCount());
        assertTrue(question.getOptions().isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddOptionWithNull() {
        question.addOption(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddOptionWithEmpty() {
        question.addOption("   ");
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveOptionWithNegativeIndex() {
        question.addOption("Option A");
        question.removeOption(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveOptionWithIndexOutOfBounds() {
        question.addOption("Option A");
        question.removeOption(5);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetOptionsWithInvalidOption() {
        ArrayList<String> invalidOptions = new ArrayList<>(Arrays.asList("Valid", null, "Also valid"));
        question.setOptions(invalidOptions);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetOptionsWithEmptyOption() {
        ArrayList<String> invalidOptions = new ArrayList<>(Arrays.asList("Valid", "", "Also valid"));
        question.setOptions(invalidOptions);
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
    
    @Test
    public void testValidOptionsWithSpaces() {
        // Opciones con espacios que se recortan son válidas
        question.addOption("  Option with spaces  ");
        assertEquals("Option with spaces", question.getOptions().get(0));
    }
}