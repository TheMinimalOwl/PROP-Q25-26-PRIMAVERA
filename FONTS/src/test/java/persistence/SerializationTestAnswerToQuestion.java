package persistence;

import com.google.gson.Gson;
import domain.AnswerFreeQuestion;
import domain.AnswerMultipleChoiceQuestion;
import domain.AnswerNumericalQuestion;
import domain.AnswerSingleChoiceQuestion;
import domain.AnswerToQuestion;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of AnswerToQuestion
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestAnswerToQuestion {

    @Test
    public void testAnswerFreeQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // Create and populate AnswerFreeQuestion
            AnswerFreeQuestion original = new AnswerFreeQuestion(1, 100, 10, "This is a free text answer");
            
            // Serialize
            String json = gson.toJson(original, AnswerToQuestion.class);
            
            // Deserialize
            AnswerToQuestion deserialized = gson.fromJson(json, AnswerToQuestion.class);
            
            // Verify
            assertTrue("Deserialized object should be instance of AnswerFreeQuestion", deserialized instanceof AnswerFreeQuestion);
            AnswerFreeQuestion result = (AnswerFreeQuestion) deserialized;
            
            assertEquals("IdUser should match", original.getIdUser(), result.getIdUser());
            assertEquals("IdForm should match", original.getIdForm(), result.getIdForm());
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("AnswerText should match", original.getAnswerText(), result.getAnswerText());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testAnswerMultipleChoiceQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // Create and populate AnswerMultipleChoiceQuestion
            ArrayList<Integer> selectedOptions = new ArrayList<>(Arrays.asList(0, 2, 4));
            AnswerMultipleChoiceQuestion original = new AnswerMultipleChoiceQuestion(2, 100, 20, selectedOptions);
            
            // Serialize
            String json = gson.toJson(original, AnswerToQuestion.class);
            
            // Deserialize
            AnswerToQuestion deserialized = gson.fromJson(json, AnswerToQuestion.class);
            
            // Verify
            assertTrue("Deserialized object should be instance of AnswerMultipleChoiceQuestion", deserialized instanceof AnswerMultipleChoiceQuestion);
            AnswerMultipleChoiceQuestion result = (AnswerMultipleChoiceQuestion) deserialized;
            
            assertEquals("IdUser should match", original.getIdUser(), result.getIdUser());
            assertEquals("IdForm should match", original.getIdForm(), result.getIdForm());
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("SelectedOptionIndexes should match", original.getSelectedOptionIndexes(), result.getSelectedOptionIndexes());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testAnswerNumericalQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // Create and populate AnswerNumericalQuestion
            AnswerNumericalQuestion original = new AnswerNumericalQuestion(3, 100, 30, 42);
            
            // Serialize
            String json = gson.toJson(original, AnswerToQuestion.class);
            
            // Deserialize
            AnswerToQuestion deserialized = gson.fromJson(json, AnswerToQuestion.class);
            
            // Verify
            assertTrue("Deserialized object should be instance of AnswerNumericalQuestion", deserialized instanceof AnswerNumericalQuestion);
            AnswerNumericalQuestion result = (AnswerNumericalQuestion) deserialized;
            
            assertEquals("IdUser should match", original.getIdUser(), result.getIdUser());
            assertEquals("IdForm should match", original.getIdForm(), result.getIdForm());
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("NumericalValue should match", original.getNumericalValue(), result.getNumericalValue());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testAnswerSingleChoiceQuestionDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // Create and populate AnswerSingleChoiceQuestion
            AnswerSingleChoiceQuestion original = new AnswerSingleChoiceQuestion(4, 100, 40, 1);
            
            // Serialize
            String json = gson.toJson(original, AnswerToQuestion.class);
            
            // Deserialize
            AnswerToQuestion deserialized = gson.fromJson(json, AnswerToQuestion.class);
            
            // Verify
            assertTrue("Deserialized object should be instance of AnswerSingleChoiceQuestion", deserialized instanceof AnswerSingleChoiceQuestion);
            AnswerSingleChoiceQuestion result = (AnswerSingleChoiceQuestion) deserialized;
            
            assertEquals("IdUser should match", original.getIdUser(), result.getIdUser());
            assertEquals("IdForm should match", original.getIdForm(), result.getIdForm());
            assertEquals("IdQuestion should match", original.getIdQuestion(), result.getIdQuestion());
            assertEquals("SelectedOptionIndex should match", original.getSelectedOptionIndex(), result.getSelectedOptionIndex());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }
}
