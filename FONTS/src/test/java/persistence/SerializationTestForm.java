package persistence;

import com.google.gson.Gson;
import domain.Form;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of Form
 * Serialization of Answer shoud be trivial, there is no polymorphism
 * and no type adapters used
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestForm {

    @Test
    public void testFormSerialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create Form
            Integer idForm = 10;
            String name = "Test Form";
            Integer userPropietary = 50;
            ArrayList<Integer> questions = new ArrayList<>(Arrays.asList(1, 2, 3));
            ArrayList<Integer> answers = new ArrayList<>(Arrays.asList(101, 102));
            ArrayList<Integer> analyses = new ArrayList<>(Arrays.asList(5));
            boolean isPublished = true;
            
            Form originalForm = new Form(idForm, name, userPropietary, questions, answers, analyses, isPublished);
            
            // 2. Serialize
            String json = gson.toJson(originalForm);
            System.out.println("Form JSON: " + json);
            
            // 3. Deserialize
            Form deserializedForm = gson.fromJson(json, Form.class);
            
            // 4. Verify
            assertNotNull(deserializedForm);
            assertEquals(originalForm.getIdForm(), deserializedForm.getIdForm());
            assertEquals(originalForm.getName(), deserializedForm.getName());
            assertEquals(originalForm.getUserPropietary(), deserializedForm.getUserPropietary());
            assertEquals(originalForm.isPublished(), deserializedForm.isPublished());
            
            assertEquals(originalForm.getQuestions().size(), deserializedForm.getQuestions().size());
            assertEquals(originalForm.getQuestions(), deserializedForm.getQuestions());
            
            assertEquals(originalForm.getAnswers().size(), deserializedForm.getAnswers().size());
            assertEquals(originalForm.getAnswers(), deserializedForm.getAnswers());
            
            assertEquals(originalForm.getAnalyze().size(), deserializedForm.getAnalyze().size());
            assertEquals(originalForm.getAnalyze(), deserializedForm.getAnalyze());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during Form serialization test: " + e.getMessage());
        }
    }
}