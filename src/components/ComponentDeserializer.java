/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author txaber gardeazabal
 */
public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component>{

    @Override
    public JsonElement serialize(Component t, Type type, JsonSerializationContext jsc) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(t.getClass().getCanonicalName()));
        result.add("properties", jsc.serialize(t, t.getClass()));
        return result;
    }

    @Override
    public Component deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        String ofInstance = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
        
        try  {
            return jdc.deserialize(element, Class.forName(ofInstance));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: "+ofInstance,e);
        }
    }
    
}
