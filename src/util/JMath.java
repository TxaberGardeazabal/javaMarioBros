package util;

import org.joml.Vector2f;

/**
 * Funciones matematicas de ayuda
 * @author txaber gardeazabal
 */
public class JMath {

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        // get the position relative to absolute origin (0,0)
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        // calculate the cosine and sine of the angle
        float cos = (float)Math.cos(Math.toRadians(angleDeg));
        float sin = (float)Math.sin(Math.toRadians(angleDeg));

        // calculate the new position using the formula
        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        // apply the new position with the correct origin
        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
    
    public static float lerp(float a, float b, float f)
    {
        return (float) (a * (1.0 - f) + (b * f));
    }
    
    /**
    * Normalize x.
    * @param x The value to be normalized.
    * @return The result of the normalization.
    */
    public static float normalize(float x, float dataHigh, float dataLow, float normalizedHigh, float normalizedLow) {
        return ((x - dataLow) 
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
    
    public static float flip(float x) {
        return 1 - x;
    }
    
    public static float easeIn(float x) {
        return x*x;
    }
    
    public static float easeOut(float x) {
        return flip(flip(x) * flip(x));
    }
    
    public static float easeInOut(float x) {
        return lerp(easeIn(x), easeOut(x), x);
    }
    
    /*
        ease in = x^2  incrementa el exponente para mayor curva
        flip = 1 - x  invertir
        ease out = 1 - ((1 - x)^2) incrementa el exponente para mayor curva
        ease in out = lerp(x^2, 1 - ((1 - x)^2), x)
        spike = if(x <= 0.5 ){return x}else{return (1 - x)} empieza y acaba en el mismo lugar
    */
}