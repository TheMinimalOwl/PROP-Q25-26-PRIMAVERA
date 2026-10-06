package persistence;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import domain.AnalyzeStrategy;
import domain.AnonymousUser;
import domain.AnswerFreeQuestion;
import domain.AnswerMultipleChoiceQuestion;
import domain.AnswerNumericalQuestion;
import domain.AnswerSingleChoiceQuestion;
import domain.AnswerToQuestion;
import domain.FreeQuestion;
import domain.KMeansStrategy;
import domain.KMedoidsStrategy;
import domain.MultipleChoiceQuestion;
import domain.NumericalQuestion;
import domain.OrderedSingleQuestion;
import domain.Question;
import domain.RegisteredUser;
import domain.User;

/**
 * Factory class for creating configured Gson instances.
 * Defines type adapters for LocalDateTime and AnalyzeStrategy
 * Registers type adapters for polymorphic types using RuntimeTypeAdapterFactory.
*
* @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
*/
public class GsonFactory {
    // This class is tested in all Unit tests SerializeXTest

    // Type adapters

    /**
     * Type adapter for AnalyzeStrategy defining serializer and deserializer.
     * Just stores the type of algorithm KMeans/KMedoids and restores by calling singleton getInstance.
     */
    private static class AnalyzeStrategyAdapter implements JsonSerializer<AnalyzeStrategy>, JsonDeserializer<AnalyzeStrategy> {
    
        @Override
        public JsonElement serialize(AnalyzeStrategy src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            if (src instanceof KMeansStrategy) {
                jsonObject.addProperty("algorithm", "kmeans");
            } else if (src instanceof KMedoidsStrategy) {
                jsonObject.addProperty("algorithm", "kmedoids");
            }
            return jsonObject;
        }
    
        @Override
        public AnalyzeStrategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            if (!jsonObject.has("algorithm")) {
                 throw new JsonParseException("Missing 'algorithm' field in AnalyzeStrategy JSON");
            }
            String type = jsonObject.get("algorithm").getAsString();
    
            if ("kmeans".equals(type)) {
                return KMeansStrategy.getInstance();
            } else if ("kmedoids".equals(type)) {
                return KMedoidsStrategy.getInstance();
            }
            
            throw new JsonParseException("Unknown strategy type: " + type);
        }
    }
    
    /**
     * Type adapter for LocalDateTime. Gson can not serialize LocalDateTime by default.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }
    
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
    

    // GsonFactory

    private static Gson gson;
    
    /**
     * Returns a configured Gson instance with all necessary type adapters.
     * Uses lazy initialization and caches the instance.
    *
    * @return configured Gson instance
    */
   protected static Gson getGson() {
       if (gson == null) {
            gson = createGson();
        }
        return gson;
    }

    /**
     * Creates a new configured Gson instance.
     *
     * @return new Gson instance with type adapters registered
     */
    private static Gson createGson() {
        // Adapter for User hierarchy
        RuntimeTypeAdapterFactory<User> userAdapterFactory = RuntimeTypeAdapterFactory.of(User.class, "_type")
                .registerSubtype(RegisteredUser.class, "RegisteredUser")
                .registerSubtype(AnonymousUser.class, "AnonymousUser")
                .registerSubtype(User.class, "User");

        // Adapter for Question hierarchy
        RuntimeTypeAdapterFactory<Question> questionAdapterFactory = RuntimeTypeAdapterFactory.of(Question.class, "_type")
                .registerSubtype(FreeQuestion.class, "FreeQuestion")
                .registerSubtype(MultipleChoiceQuestion.class, "MultipleChoiceQuestion")
                .registerSubtype(NumericalQuestion.class, "NumericalQuestion")
                .registerSubtype(OrderedSingleQuestion.class, "OrderedSingleQuestion");

        // Adapter for AnswerToQuestion hierarchy
        RuntimeTypeAdapterFactory<AnswerToQuestion> answerAdapterFactory = RuntimeTypeAdapterFactory.of(AnswerToQuestion.class, "type")
                .registerSubtype(AnswerFreeQuestion.class, "free")
                .registerSubtype(AnswerMultipleChoiceQuestion.class, "multiple")
                .registerSubtype(AnswerNumericalQuestion.class, "numerical")
                .registerSubtype(AnswerSingleChoiceQuestion.class, "single");

        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(userAdapterFactory)
                .registerTypeAdapterFactory(questionAdapterFactory)
                .registerTypeAdapterFactory(answerAdapterFactory)
                // Adapter for AnalyzeStrategy, serializes only the algorithm name and deserializes using singleton get instance
                .registerTypeAdapter(AnalyzeStrategy.class, new AnalyzeStrategyAdapter())
                // Adapter for LocalDateTime
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}

