package domain;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Test suite para la clase MultipleChoiceQuestion - Versión JUnit 4
 */
public class MultipleChoiceQuestionTest {

    private MultipleChoiceQuestion question;
    
    @Before
    public void setUp() {
        question = new MultipleChoiceQuestion();
    }
    
@Test
public void testDefaultConstructor() {
    assertEquals(-1, question.getIdQuestion());
    assertEquals("", question.getStatement());
    assertFalse(question.isOptional());
    assertTrue(question.getOptions().isEmpty());
    assertEquals(1, question.getMinSelections());
    assertEquals(1, question.getMaxSelections());
}
    
    @Test
    public void testParameterizedConstructorBasic() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Option 1", "Option 2", "Option 3"));
        question = new MultipleChoiceQuestion(1, "Test statement", false, options);
        
        assertEquals(1, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertFalse(question.isOptional());
        assertEquals(3, question.getOptionCount());
        assertEquals(1, question.getMinSelections());
        assertEquals(1, question.getMaxSelections());
    }
    
    @Test
    public void testParameterizedConstructorComplete() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        question = new MultipleChoiceQuestion(2, "Test statement", true, options, 1, 2);
        
        assertEquals(2, question.getIdQuestion());
        assertEquals("Test statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(4, question.getOptionCount());
        assertEquals(1, question.getMinSelections());
        assertEquals(2, question.getMaxSelections());
    }
    
    @Test
    public void testSettersAndGetters() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("X", "Y", "Z"));
        
        question.setIdQuestion(10);
        question.setStatement("New statement");
        question.setOptional(true);
        question.setOptions(options);
        question.setSelectionLimits(0, 3); // Usar setSelectionLimits en lugar de setters individuales
        
        assertEquals(10, question.getIdQuestion());
        assertEquals("New statement", question.getStatement());
        assertTrue(question.isOptional());
        assertEquals(3, question.getOptionCount());
        assertEquals(0, question.getMinSelections());
        assertEquals(3, question.getMaxSelections());
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
    public void testRemoveOption() {
        question.addOption("Option A");
        question.addOption("Option B");
        question.addOption("Option C");
        
        question.removeOption(1); // Remover "Option B"
        
        assertEquals(2, question.getOptionCount());
        assertTrue(question.getOptions().contains("Option A"));
        assertTrue(question.getOptions().contains("Option C"));
        assertFalse(question.getOptions().contains("Option B"));
    }
    
    @Test
    public void testClearOptions() {
        question.addOption("Option 1");
        question.addOption("Option 2");
        question.clearOptions();
        
        assertTrue(question.getOptions().isEmpty());
        assertEquals(0, question.getOptionCount());
        assertEquals(0, question.getMinSelections());
        assertEquals(0, question.getMaxSelections());
    }
    
    @Test
    public void testGetOptionsAsString() {
        question.addOption("Red");
        question.addOption("Green");
        question.addOption("Blue");
        
        String optionsString = question.getOptionsAsString();
        
        assertTrue(optionsString.contains("1. Red"));
        assertTrue(optionsString.contains("2. Green"));
        assertTrue(optionsString.contains("3. Blue"));
        assertTrue(optionsString.contains("; "));
    }
    
    @Test
    public void testGetOptionsAsStringEmpty() {
        String optionsString = question.getOptionsAsString();
        assertEquals("", optionsString);
    }
    
    @Test
    public void testToString() {
        ArrayList<String> options = new ArrayList<>(Arrays.asList("Yes", "No"));
        question = new MultipleChoiceQuestion(5, "Test question", true, options, 1, 2);
        String result = question.toString();
        
        assertTrue(result.contains("id=5"));
        assertTrue(result.contains("statement='Test question'"));
        assertTrue(result.contains("optional=true"));
        assertTrue(result.contains("options=2"));
        assertTrue(result.contains("selections=1-2"));
        assertTrue(result.contains("MultipleChoiceQuestion"));
    }
    
    @Test
    public void testEqualsMethod() {
        MultipleChoiceQuestion q1 = new MultipleChoiceQuestion(1, "Q1", false, new ArrayList<>());
        MultipleChoiceQuestion q2 = new MultipleChoiceQuestion(1, "Q2", true, new ArrayList<>());
        MultipleChoiceQuestion q3 = new MultipleChoiceQuestion(2, "Q1", false, new ArrayList<>());
        
        assertTrue(q1.equals(q2)); // Mismo ID
        assertFalse(q1.equals(q3)); // Diferente ID
    }
    
    @Test
    public void testSelectionRangeValidation() {
        // Usar setSelectionLimits para establecer ambos valores simultáneamente
        question.setSelectionLimits(2, 3);
        
        assertEquals(2, question.getMinSelections());
        assertEquals(3, question.getMaxSelections());
    }
    
    @Test
    public void testIndividualSelectionSetters() {
        // Primero establecer maxSelections, luego minSelections
        question.setMaxSelections(3);
        question.setMinSelections(2);
        
        assertEquals(2, question.getMinSelections());
        assertEquals(3, question.getMaxSelections());
    }
    
    @Test
    public void testSelectionRangeWithOptions() {
        // Añadir opciones primero
        question.addOption("Option 1");
        question.addOption("Option 2");
        question.addOption("Option 3");
        
        question.setSelectionLimits(1, 3);
        
        assertEquals(1, question.getMinSelections());
        assertEquals(3, question.getMaxSelections());
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
    public void testSetNegativeMinSelections() {
        question.setMinSelections(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeMaxSelections() {
        question.setMaxSelections(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMinSelectionsGreaterThanMax() {
        // Intentar establecer min > max con los valores por defecto
        question.setMinSelections(2); // 2 > 1 (max por defecto)
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxSelectionsLessThanMin() {
        // Primero establecer un min válido
        question.setMaxSelections(3);
        question.setMinSelections(2);
        // Luego intentar establecer un max menor que el min actual
        question.setMaxSelections(1); // 1 < 2
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxSelectionsExceedsOptions() {
        question.addOption("A");
        question.addOption("B");
        question.setMaxSelections(3); // Solo hay 2 opciones
    }
    
    @Test
    public void testValidSelectionLimitsWithOptions() {
        question.addOption("A");
        question.addOption("B");
        question.addOption("C");
        
        question.setSelectionLimits(1, 3); // Válido: máximo igual al número de opciones
        assertEquals(1, question.getMinSelections());
        assertEquals(3, question.getMaxSelections());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNullStatement() {
        question.setStatement(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyStatement() {
        question.setStatement("   ");
    }
    
    @Test
    public void testSetSelectionLimitsValid() {
        question.setSelectionLimits(0, 5);
        assertEquals(0, question.getMinSelections());
        assertEquals(5, question.getMaxSelections());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetSelectionLimitsInvalid() {
        question.setSelectionLimits(3, 2); // min > max
    }
    
    @Test
    public void testDefaultValuesAfterClearOptions() {
        question.addOption("Option 1");
        question.addOption("Option 2");
        question.setSelectionLimits(1, 2);
        
        question.clearOptions();
        
        assertTrue(question.getOptions().isEmpty());
        assertEquals(0, question.getMinSelections());
        assertEquals(0, question.getMaxSelections());
    }
}