/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import editor.OImGui;
import gameEngine.GameObject;
import imgui.ImGui;
import imgui.type.ImInt;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * functionalities for gameobjects
 * @author txaber
 */
public abstract class Component {
    
    private static int ID_COUNTER = 0;
    private int uid = -1;
    
    public transient GameObject gameObject = null;
    
    public void start() {}
    
    public void update(float dt){}
    
    public void editorUpdate(float dt) {}
    
    public void beginCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {}
    
    public void endCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {}
    
    public void preSolve(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {}
    
    public void postSolve(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {}
    
    public void imGui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field:fields) {
                // we don't expose transient variables, skip if found
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }
                
                // to get private variables me must temporaly gain access to them
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }
                boolean isProtected = Modifier.isProtected(field.getModifiers());
                if (isProtected) {
                    field.setAccessible(true);
                }
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();
                
                if (type == int.class) {
                    int val = (int)value;
                     field.set(this, OImGui.dragInt(name, val));
                } else if (type == float.class) {
                    float val = (float)value;
                    field.set(this, OImGui.dragFloat(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name+": ", val)) {
                        field.set(this, !val);
                    } 
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f)value;
                    OImGui.drawVec2Control(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x,val.y,val.z};
                    if (ImGui.dragFloat3(name+": ", imVec)) {
                        val.set(imVec[0],imVec[1],imVec[2]);
                    } 
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = {val.x,val.y,val.z,val.w};
                    if (ImGui.dragFloat4(name+": ", imVec)) {
                        val.set(imVec[0],imVec[1],imVec[2],imVec[3]);
                    } 
                } else if (type.isEnum()) {
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if (type == String.class) {
                    field.set(this, OImGui.inputText(field.getName() + ": ", (String)value));
                }
                else {
                    // lots of unhandled types
                    //System.out.println("found unhandled variable type: "+ type.toString());
                }
                
                if (isPrivate) {
                    field.setAccessible(false);
                }
                if (isProtected) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void generateId() {
        if (uid == -1) {
            uid = ID_COUNTER++;
        }
    }

    public int getUid() {
        return uid;
    }
    
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }
    
    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }
    
    private int indexOf (String str, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (str.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    public void destroy() {
        // empty
    }
    
    
}
