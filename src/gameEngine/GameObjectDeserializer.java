/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameEngine;

import components.Component;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author txaber gardeazabal
 */
public class GameObjectDeserializer implements JsonDeserializer<GameObject>{

    @Override
    public GameObject deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");
        JsonArray children = jsonObject.getAsJsonArray("childGOs");
        
        GameObject go = new GameObject(name);
        for (JsonElement e: components){
            Component c = jdc.deserialize(e, Component.class);
            go.addComponent(c);
        }
        for (JsonElement e: children){
            GameObject goc = jdc.deserialize(e, GameObject.class);
            go.addChild(goc);
        }
        go.transform = go.getComponent(Transform.class);
        
        return go;
    }
}
