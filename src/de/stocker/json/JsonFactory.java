package de.stocker.json;

import com.google.gson.*;

/**
 * A factory for creating Java objects from JSON strings and creating JSON
 * strings from Java objects. Used to translate the data coming from the data
 * providers in JSON format to the Java classes of the data base model, and to
 * translate Java objects to JSON formatted strings in order to write them to
 * disk for persistence of the application state.
 * 
 * @author Matthias Rudolph
 */
public final class JsonFactory {

    private static Gson gson = new GsonBuilder().create();
    
    private JsonFactory() {}
    
    /**
     * Produces a Java object from a JSON string. Method is parameterized and
     * works for all classes offering the matching fields for a specific JSON
     * input string.
     *
     * @param <T> the generic type
     * @param json the JSON input string
     * @param classOfT the class of the generic type of the target object (append ".class")
     * @return an object of the specified type
     */
    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    /**
     * Produces a JSON formatted string from a Java object of the specified
     * type.
     *
     * @param <T> the generic type of the input object
     * @param inputObject the input object
     * @return the JSON formatted string
     */
    public static <T> String objectToJson(T inputObject) {
        return gson.toJson(inputObject);
    }
    
}
