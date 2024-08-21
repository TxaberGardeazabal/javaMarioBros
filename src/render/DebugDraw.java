/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

import gameEngine.Camera;
import gameEngine.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glLineWidth;
import util.AssetPool;
import util.JMath;

/**
 * Clase que dibuja lineas y otras formas geometricas en pantalla para debugear.
 * Esta clase funciona de manera estatica, las nuevas lineas se añaden con llamadas a esta clase, todas las lineas se dibujan despues de dibijar la escena y encima de todos los gameobject. 
 * @author txaber gardeazabal
 */
public class DebugDraw {
    private static final int MAX_LINES = 3000;
    
    private static List<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex,2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");
    
    private static int vaoID;
    private static int vboID;

    private static boolean started = false;
    
    /**
     * inicializa esta clase en la GPU, llamado antes del primer frame.
     */
    public static void start() {
        // generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        // generate the vbo and buffer some memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);
        
        // enable the vertex array attributes
        glVertexAttribPointer(0,3,GL_FLOAT,false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        glVertexAttribPointer(1,3,GL_FLOAT,false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        
        glLineWidth(2.0f);
    }
    
    /**
     * Prepara para dibujar la siguiente frame
     */
    public static void beginFrame(){
        if (!started) {
            start();
            started = true;
        }
        
        // remove dead lines
        for (int i = lines.size(); i > 0; i--) {
            if (lines.get(i-1).beginFrame() < 0) {
                lines.remove(i-1);
            }
        }
    }
    
    /**
     * Dibuja todas las lineas y formas guardadas en la pantalla
     */
    public static void draw(){
        if (lines.size() <= 0) return;
            
        int index = 0;
        for (Line2D line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getStart() : line.getEnd();
                Vector3f color = line.getColor();
                
                // load postion
                vertexArray[index] = position.x;
                vertexArray[index+1] = position.y;
                vertexArray[index+2] = -10.0f;
                
                // load color
                vertexArray[index+3] = color.x;
                vertexArray[index+4] = color.y;
                vertexArray[index+5] = color.z;
                
                index += 6;
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER,0,Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));
        
        // use our shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        
        // bind the vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        // draw the batch
        glDrawArrays(GL_LINES,0,lines.size());
        
        // disable location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        
        // unbind shader
        shader.detach();
    }
    
    // add line2D methods
    // =====================
    public static void addLine2D(Vector2f start,Vector2f end){
        addLine2D(start, end, new Vector3f(0,1,0),1);
    }
    
    public static void addLine2D(Vector2f start,Vector2f end,Vector3f color){
        addLine2D(start, end, color,1);
    }
    
    /**
     * Guarda una linea para dibujar
     * @param start punto A de la linea
     * @param end punto B de la linea
     * @param color color rgb de la linea
     * @param lifetime cantidad de frames donde se va a dibujar la linea antes de borrarlo de la pantalla
     */
    public static void addLine2D(Vector2f start,Vector2f end, Vector3f color, int lifetime){
        Camera camera = Window.getScene().camera();
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f,-2.0f));
        Vector2f cameraRight = new Vector2f(camera.position)
                .add(new Vector2f(camera.getProjectionSize())
                .mul(camera.getZoom()))
                .add(new Vector2f(4.0f,4.0f));
        boolean lineInView = ((start.x >= cameraLeft.x && start.x <= cameraRight.x) 
                && (start.y >= cameraLeft.y && start.y <= cameraRight.y))
                || ((end.x >= cameraLeft.x && end.x <= cameraRight.x) 
                && (end.y >= cameraLeft.y && end.y <= cameraRight.y));
        if (lines.size() >= MAX_LINES ||!lineInView) return;  
        DebugDraw.lines.add(new Line2D(start, end, color, lifetime));
    }
    
    // add box2D methods
    // =====================
    public static void addBox2D(Vector2f center,Vector2f dimentions, float rotation){
        addBox2D(center, dimentions, rotation,new Vector3f(0,1,0),1);
    }
    
    public static void addBox2D(Vector2f center,Vector2f dimentions, float rotation,Vector3f color){
        addBox2D(center, dimentions, rotation,color,1);
    }
    
    /**
     * Guarda 4 lineas para dibujar un cuadrado.
     * @param center posicion del centro del cuadrado
     * @param dimentions dimensiones en x e y del cuadrado (pueden ser diferentes)
     * @param rotation rotacion del cuadrado
     * @param color color de las lineas del cuadrado
     * @param lifetime cantidad de frames donde se va a dibujar la linea antes de borrarlo de la pantalla
     */
    public static void addBox2D(Vector2f center,Vector2f dimentions, float rotation, Vector3f color, int lifetime){
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimentions).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimentions).mul(0.5f));
        Vector2f vertices[] = {
            new Vector2f(min.x,min.y),
            new Vector2f(min.x,max.y),
            new Vector2f(max.x,max.y),
            new Vector2f(max.x,min.y)  
        };
        
        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }
        
        addLine2D(vertices[0],vertices[1],color,lifetime);
        addLine2D(vertices[1],vertices[2],color,lifetime);
        addLine2D(vertices[2],vertices[3],color,lifetime);
        addLine2D(vertices[3],vertices[0],color,lifetime);
    }
    
    // add circle2D methods
    // =====================
    public static void addCircle2D(Vector2f center,float radius){
        addCircle2D(center, radius, new Vector3f(0,1,0),1);
    }
    
    public static void addCircle2D(Vector2f center,float radius,Vector3f color){
        addCircle2D(center, radius, color,1);
    }
    
    /**
     * Guarda 20 lineas para dibujar un circulo
     * @param center posicion del centro del circulo
     * @param radius radio del circulo
     * @param color color rgb de la circumferencia
     * @param lifetime cantidad de frames donde se va a dibujar la linea antes de borrarlo de la pantalla
     */
    public static void addCircle2D(Vector2f center,float radius, Vector3f color, int lifetime){
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;
        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius,0);
            JMath.rotate(tmp, currentAngle, new Vector2f());
            
            points[i] = new Vector2f(tmp).add(center);
            if (i > 0) {
                addLine2D(points[i -1],points[i],color,lifetime);
            }
            currentAngle += increment;
        }
        // last line
        addLine2D(points[points.length -1],points[0],color,lifetime);
    }
    
    
}
