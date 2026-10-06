package persistence;

import com.google.gson.Gson;
import domain.RegisteredUser;
import domain.User;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import domain.AnonymousUser;
import static org.junit.Assert.*;

/**
 * Unit test for the serialization of User
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class SerializationTestUser {
    @Test
    public void printJsonOutput() {
        try {
            Gson gson = GsonFactory.getGson();
            
            RegisteredUser user = new RegisteredUser("testuser", "password123", "test@example.com");
            user.setIdUser(1);
            user.setAnswers(new ArrayList<>(Arrays.asList(10, 20, 30)));

            ArrayList<Integer> forms = new ArrayList<>(Arrays.asList(100, 200));
            user.setForms(forms);
            
            // Serialitzar com a user pare, utilitza es type adapter
            System.out.println("Serializing as User.class:");
            String jsonUser = gson.toJson(user, User.class);
            System.out.println(jsonUser);
            
            // Serialitza per defecte com a RegisteredUser, no tocaria sortir _type
            System.out.println("\nSerializing as RegisteredUser.class (inferred):");
            String jsonReg = gson.toJson(user);
            System.out.println(jsonReg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegisteredUserDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate RegisteredUser
            RegisteredUser originalUser = new RegisteredUser("testuser", "password123", "test@example.com");
            originalUser.setIdUser(1);
            ArrayList<Integer> answers = new ArrayList<>(Arrays.asList(10, 20, 30));
            originalUser.setAnswers(answers);
            ArrayList<Integer> forms = new ArrayList<>(Arrays.asList(100, 200));
            originalUser.setForms(forms);
            
            // 2. Serialize
            String json = gson.toJson(originalUser, User.class);
            
            // 3. Deserialize
            User deserializedUser = gson.fromJson(json, User.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of RegisteredUser", deserializedUser instanceof RegisteredUser);
            RegisteredUser result = (RegisteredUser) deserializedUser;
            
            assertEquals("IdUser should match", originalUser.getIdUser(), result.getIdUser());
            assertEquals("Username should match", originalUser.getUsername(), result.getUsername());
            assertEquals("Password should match", originalUser.getPassword(), result.getPassword());
            assertEquals("Email should match", originalUser.getEmail(), result.getEmail());
            assertEquals("Answers should match", originalUser.getAnswers(), result.getAnswers());
            assertEquals("Forms should match", originalUser.getForms(), result.getForms());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }

    @Test
    public void testAnonymousUserDeserialization() {
        try {
            Gson gson = GsonFactory.getGson();
            
            // 1. Create and populate AnonymousUser
            AnonymousUser originalUser = new AnonymousUser(2);
            ArrayList<Integer> answers = new ArrayList<>(Arrays.asList(40, 50));
            originalUser.setAnswers(answers);
            
            // 2. Serialize
            String json = gson.toJson(originalUser, User.class);
            
            // 3. Deserialize
            User deserializedUser = gson.fromJson(json, User.class);
            
            // 4. Verify
            assertTrue("Deserialized object should be instance of AnonymousUser", deserializedUser instanceof AnonymousUser);
            AnonymousUser result = (AnonymousUser) deserializedUser;
            
            assertEquals("IdUser should match", originalUser.getIdUser(), result.getIdUser());
            assertEquals("Answers should match", originalUser.getAnswers(), result.getAnswers());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during serialization test: " + e.getMessage());
        }
    }
}
