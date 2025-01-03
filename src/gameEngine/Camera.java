/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * La camara sirve para poder visualizar el mundo, por defecto ortografico, se pueden ajustar diferentes puntos de vista en la pantalla
 * @author txaber
 */
public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;
    
    private float projectionWidth = 6;
    private float projectionHeight = 3;
    // para al aspect ratio original usar 6 / 3.5 (original es 4 / 3)
    public Vector4f clearColor = new Vector4f(1,1,1,1);
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
    
    private float zoom = 1.0f;
    private float zoomLimitMax = 3.0f;
    private float zoomLimitMin = 0.5f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }
    
    /**
     * Ajusta la proyeccion
     */
    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f,100.0f);
        projectionMatrix.invert(inverseProjection);
    }
    
    public Matrix4f getViewMatrix() {
        Vector3f camerafront = new Vector3f(0.0f,0.0f,-1.0f);
        Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x,position.y, 20.0f),
                                            camerafront.add(position.x,position.y, 0.0f),
                                            cameraUp);
        this.viewMatrix.invert(inverseView);
        
        return this.viewMatrix;
    }
    
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public float getZoom() {
        return zoom;
    }

    /**
     * Ajusta el zoom en la camara
     * @param zoom nuevo valor
     */
    public void setZoom(float zoom) {
        if (zoom >= zoomLimitMax) {
            this.zoom = zoomLimitMax;
        } else if (zoom <= zoomLimitMin) {
            this.zoom = zoomLimitMin;
        } else {
            this.zoom = zoom;
        }
    }
    
    /**
     * Añade o resta zoom a la camara depende del valor pasado
     * @param value valor a añadir al zoom
     */
    public void addZoom(float value) {
        if ((this.zoom + value) > zoomLimitMax) {
            this.zoom = zoomLimitMax;
        } else if ((this.zoom + value) < zoomLimitMin) {
            this.zoom = zoomLimitMin;
        } else {
            this.zoom += value;
        }        
    }
    
    /**
     * pone el zoom a 1
     */
    public void resetZoom() {
        this.zoom = 1;
    }
}
