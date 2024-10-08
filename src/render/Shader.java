/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package render;

import editor.ConsoleWindow;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Clase para interpretar shaders glsl y mandarlos al GPU
 * @author txaber
 */
public class Shader {
    
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;
    
    private boolean beingUsed = false;
    
    public Shader(String filepath) {
        this.filepath = filepath;
        
        // ajusta el caracter final dependiendo del SO
        String eolChar;
        if (System.getProperty("os.name").equals("Windows 10")) {
            eolChar = "\r\n";
        } else {
            eolChar = "\n";
        }
        
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
            
            // find the first pattern after #type
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf(eolChar, index);
            String firstPattern = source.substring(index,eol).trim();
            
            // find the second pattern after #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf(eolChar, index);
            String secondPattern = source.substring(index,eol).trim();
            
            switch (firstPattern) {
                case "vertex":
                    this.vertexSource = splitString[1];
                    break;
                case "fragment":
                    this.fragmentSource = splitString[1];
                    break;
                default:
                    throw new IOException("Unexpected token '"+firstPattern+"'");
            }
            
            switch (secondPattern) {
                case "vertex":
                    this.vertexSource = splitString[2];
                    break;
                case "fragment":
                    this.fragmentSource = splitString[2];
                    break;
                default:
                    throw new IOException("Unexpected token '"+secondPattern+"'");
            }
        }
        catch(IOException e) {
            ConsoleWindow.addLog("could not open file for shader: "+ filepath, ConsoleWindow.LogCategory.error); 
        }
    }
    
    /**
     * Compila la informacion del shader en openGL
     */
    public void compile() {
        int vertexID, fragmentID;
        
        // load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        
        // check for error in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            ConsoleWindow.addLog("'"+filepath+" \nvertex compilation failed'", ConsoleWindow.LogCategory.error);
            ConsoleWindow.addLog(glGetShaderInfoLog(vertexID, len), ConsoleWindow.LogCategory.error);
            assert false: "";
        }
        
        
        // load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        
        // check for error in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            ConsoleWindow.addLog("'"+filepath+" \nfragment compilation failed'", ConsoleWindow.LogCategory.error);
            ConsoleWindow.addLog(glGetShaderInfoLog(fragmentID, len), ConsoleWindow.LogCategory.error);
            assert false: "";
        }
        
        // link shader and check error
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        
        // check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            ConsoleWindow.addLog("'"+filepath+" \nlinking of shaders failed'", ConsoleWindow.LogCategory.error);
            ConsoleWindow.addLog(glGetProgramInfoLog(shaderProgramID, len), ConsoleWindow.LogCategory.error);
            assert false: "";
        }
    }
    
    /**
     * Dice a openGL que este es el shader que tiene que usar, importante llamar a la funcion detach antes de llamar a use otra vez
     */
    public void use() {
        // bind shader program
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    
    /**
     * Dice a openGL que este shader no esta en uso
     */
    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }
    
    // upload functions
    /**
     * sube un matrix4f
     * @param varName nombre de variable
     * @param mat4 variable a subir
     */
    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        //System.out.println(mat4.toString());
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    
    /**
     * sube un matrix3f
     * @param varName nombre de variable
     * @param mat3 variable a subir
     */
    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
    
    /**
     * sube un vector4f
     * @param varName nombre de variable
     * @param vec variable a subir
     */
    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    
    /**
     * sube un vector3f
     * @param varName nombre de variable
     * @param vec variable a subir
     */
    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    
    /**
     * sube un vector2f
     * @param varName nombre de variable
     * @param vec variable a subir
     */
    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }
    
    /**
     * sube un float
     * @param varName nombre de variable
     * @param val variable a subir
     */
    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }
    
    /**
     * sube un int
     * @param varName nombre de variable
     * @param val variable a subir
     */
    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
    
    /**
     * sube una textura
     * @param varName nombre de variable
     * @param slot posicion del uniform destino donde se va a subir
     */
    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    /**
     * sube un array de ints
     * @param varName nombre de variable
     * @param array variable a subir
     */
    void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}
