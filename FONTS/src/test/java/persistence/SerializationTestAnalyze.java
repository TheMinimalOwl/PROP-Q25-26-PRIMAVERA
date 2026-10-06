package persistence;

import com.google.gson.Gson;
import domain.AnalysisResult;
import domain.Analyze;
import domain.AnalyzeStrategy;
import domain.AnswerFreeQuestion;
import domain.AnswerToQuestion;
import domain.KMeansStrategy;
import domain.KMedoidsStrategy;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of Analyze
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestAnalyze {

    @Test
    public void testKMeansStrategySerialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Get KMeansStrategy instance
            AnalyzeStrategy originalStrategy = KMeansStrategy.getInstance();
            
            // 2. Serialize
            String json = gson.toJson(originalStrategy, AnalyzeStrategy.class);
            System.out.println("KMeansStrategy JSON: " + json);
            
            // 3. Deserialize
            AnalyzeStrategy deserializedStrategy = gson.fromJson(json, AnalyzeStrategy.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of KMeansStrategy", deserializedStrategy instanceof KMeansStrategy);
            assertSame("Deserialized object should be the singleton instance", KMeansStrategy.getInstance(), deserializedStrategy);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during KMeansStrategy serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testKMedoidsStrategySerialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Get KMedoidsStrategy instance
            AnalyzeStrategy originalStrategy = KMedoidsStrategy.getInstance();
            
            // 2. Serialize
            String json = gson.toJson(originalStrategy, AnalyzeStrategy.class);
            System.out.println("KMedoidsStrategy JSON: " + json);
            
            // 3. Deserialize
            AnalyzeStrategy deserializedStrategy = gson.fromJson(json, AnalyzeStrategy.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of KMedoidsStrategy", deserializedStrategy instanceof KMedoidsStrategy);
            assertSame("Deserialized object should be the singleton instance", KMedoidsStrategy.getInstance(), deserializedStrategy);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during KMedoidsStrategy serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testAnalyzeSerialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Prepare data for Analyze constructor
            int idForm = 456;
            String name = "Test Analysis";
            int nClusters = 2;
            AnalyzeStrategy algorithm = KMedoidsStrategy.getInstance();
            
            ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
            ArrayList<AnswerToQuestion> user1Answers = new ArrayList<>();
            String strings[] = {"Hola", "asd", "asddsa"};
            for (int i = 0; i < 3; i++) {
                user1Answers.add(new AnswerFreeQuestion(i, idForm, i, strings[i]));
            }
            ArrayList<AnswerToQuestion> user2Answers = new ArrayList<>();
            String strings2[] = {"lkjk", "jkklj", "okjolkj"};
            for (int i = 0; i < 3; i++) {
                user2Answers.add(new AnswerFreeQuestion(i, idForm, i, strings2[i]));
            }
            ArrayList<AnswerToQuestion> user3Answers = new ArrayList<>();
            String strings3[] = {"asdffd", "jkkgfdflj", "asdfefs"};
            for (int i = 0; i < 3; i++) {
                user3Answers.add(new AnswerFreeQuestion(i, idForm, i, strings3[i]));
            }

            answers.add(user1Answers);
            answers.add(user2Answers);
            answers.add(user3Answers);
            
            // 2. Construct Analyze object (this runs the algorithm)
            Analyze originalAnalyze = new Analyze(idForm, name, nClusters, algorithm, answers);
            originalAnalyze.setIdAnalyze(123); // Set an ID for testing
            
            // 3. Serialize
            String json = gson.toJson(originalAnalyze);
            System.out.println("Analyze JSON: " + json);
            
            // 4. Deserialize
            Analyze deserializedAnalyze = gson.fromJson(json, Analyze.class);
            
            // 5. Verify
            assertNotNull(deserializedAnalyze);
            assertEquals(originalAnalyze.getIdAnalyze(), deserializedAnalyze.getIdAnalyze());
            assertEquals(originalAnalyze.getIdForm(), deserializedAnalyze.getIdForm());
            assertEquals(originalAnalyze.getName(), deserializedAnalyze.getName());
            assertEquals(originalAnalyze.getNClusters(), deserializedAnalyze.getNClusters());
            assertEquals(originalAnalyze.getTime(), deserializedAnalyze.getTime());
            
            // Verify Strategy Singleton
            //assertTrue(deserializedAnalyze.getAlgorithm() instanceof KMeansStrategy);
            //assertSame(KMeansStrategy.getInstance(), deserializedAnalyze.getAlgorithm());
            
            // Verify Result
            AnalysisResult originalResult = originalAnalyze.getResult();
            AnalysisResult deserializedResult = deserializedAnalyze.getResult();
            
            assertNotNull(deserializedResult);
            assertArrayEquals(originalResult.getClusterAssignments(), deserializedResult.getClusterAssignments());
            assertEquals(originalResult.getQuality(), deserializedResult.getQuality(), 0.001);
            
            // Verify Centroids
            assertEquals(originalResult.getCentroids().size(), deserializedResult.getCentroids().size());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during Analyze serialization test: " + e.getMessage());
        }
    }
}
