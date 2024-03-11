/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 *
 * @author txaber
 */
public class Time {
    public static double timeStarted = glfwGetTime();
    // usseless
    public static float gtTime() {
        return (float)((glfwGetTime() - timeStarted) * 1E-9);
    }
}
