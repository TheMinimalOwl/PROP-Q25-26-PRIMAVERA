package persistence;

import com.google.gson.Gson;
import domain.FreeQuestion;
import domain.MultipleChoiceQuestion;
import domain.NumericalQuestion;
import domain.OrderedSingleQuestion;
import domain.Question;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of Question
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestQuestion {

    @Test
    public void printJsonOutput() {
        try {
            Gson gson = GsonFactory.getGson();
            
            FreeQuestion question = new FreeQuestion(1, "How are you?", true);
            question.setIdForm(100);
            
            // Serialize as Question.class (uses TypeAdapter)
            System.out.println("Serializing as Question.class:");
            String jsonQuestion = gson.toJson(question, Question.class);
            System.out.println(jsonQuestion);
            
            // Serialize as FreeQuestion.class (inferred)
            System.out.println("\nSerializing as FreeQuestion.class (inferred):");
            String jsonFree = gson.toJson(question);
            System.out.println(jsonFree);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFreeQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate FreeQuestion
            FreeQuestion original = new FreeQuestion(1, "How are you?", true);
            original.setIdForm(100);
            
            // 2. Serialize
            String json = gson.toJson(original, Question.class);
            
            // 3. Deserialize
            Question deserialized = gson.fromJson(json, Question.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of FreeQuestion", deserialized instanceof FreeQuestion);
            FreeQuestion result = (FreeQuestion) deserialized;
            
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("Statement should match", original.getStatement(), result.getStatement());
            assertEquals("IsOptional should match", original.isOptional(), result.isOptional());
            assertEquals("IdForm should match", original.getIdForm(), result.getIdForm());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleChoiceQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate MultipleChoiceQuestion
            ArrayList<String> options = new ArrayList<>(Arrays.asList("A", "B", "C"));
            MultipleChoiceQuestion original = new MultipleChoiceQuestion(2, "Choose one", false, options);
            original.setIdForm(100);
            
            // 2. Serialize
            String json = gson.toJson(original, Question.class);
            
            // 3. Deserialize
            Question deserialized = gson.fromJson(json, Question.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of MultipleChoiceQuestion", deserialized instanceof MultipleChoiceQuestion);
            MultipleChoiceQuestion result = (MultipleChoiceQuestion) deserialized;
            
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("Statement should match", original.getStatement(), result.getStatement());
            assertEquals("Options should match", original.getOptions(), result.getOptions());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testNumericalQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate NumericalQuestion
            NumericalQuestion original = new NumericalQuestion(3, "Rate 1-10", true);
            original.setIdForm(100);
            original.setMinValue(1);
            original.setMaxValue(10);
            
            // 2. Serialize
            String json = gson.toJson(original, Question.class);
            
            // 3. Deserialize
            Question deserialized = gson.fromJson(json, Question.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of NumericalQuestion", deserialized instanceof NumericalQuestion);
            NumericalQuestion result = (NumericalQuestion) deserialized;
            
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("Statement should match", original.getStatement(), result.getStatement());
            assertEquals("MinValue should match", original.getMinValue(), result.getMinValue());
            assertEquals("MaxValue should match", original.getMaxValue(), result.getMaxValue());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testOrderedSingleQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate OrderedSingleQuestion
            ArrayList<String> options = new ArrayList<>(Arrays.asList("First", "Second"));
            OrderedSingleQuestion original = new OrderedSingleQuestion(4, "Order these", false, options);
            original.setIdForm(100);
            
            // 2. Serialize
            String json = gson.toJson(original, Question.class);
            
            // 3. Deserialize
            Question deserialized = gson.fromJson(json, Question.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of OrderedSingleQuestion", deserialized instanceof OrderedSingleQuestion);
            OrderedSingleQuestion result = (OrderedSingleQuestion) deserialized;
            
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("Statement should match", original.getStatement(), result.getStatement());
            assertEquals("Options should match", original.getOptions(), result.getOptions());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }
}
