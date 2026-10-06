package persistence;

import com.google.gson.Gson;
import domain.Answer;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of Answer
 * Serialization of Answer shoud be trivial, there is no polymorphism
 * and no type adapters used
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestAnswer {

    @Test
    public void testAnswerSerialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create Answer
            Integer idForm = 1;
            Integer idUser = 100;
            Answer originalAnswer = new Answer(idForm, idUser);
            
            // Add some responses
            originalAnswer.addResponse(10);
            originalAnswer.addResponse(20);
            originalAnswer.addResponse(30);
            
            // 2. Serialize
            String json = gson.toJson(originalAnswer);
            System.out.println("Answer JSON: " + json);
            
            // 3. Deserialize
            Answer deserializedAnswer = gson.fromJson(json, Answer.class);
            
            // 4. Verify
            assertNotNull(deserializedAnswer);
            assertEquals(originalAnswer.getIdForm(), deserializedAnswer.getIdForm());
            assertEquals(originalAnswer.getIdUser(), deserializedAnswer.getIdUser());
            assertEquals(originalAnswer.getResponses().size(), deserializedAnswer.getResponses().size());
            assertEquals(originalAnswer.getResponses(), deserializedAnswer.getResponses());
            
            // Verify time (might have slight precision differences, but should be close or equal depending on adapter)
            assertEquals(originalAnswer.getTime(), deserializedAnswer.getTime());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during Answer serialization test: " + e.getMessage());
        }
    }
}