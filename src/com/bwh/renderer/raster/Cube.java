package com.bwh.renderer.raster;

import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;

public class Cube {
    /*
    var vertices = [
    vec4(-0.5, -0.5, 0.5, 1.0),
    vec4(-0.5, 0.5, 0.5, 1.0),
    vec4(0.5, 0.5, 0.5, 1.0),
    vec4(0.5, -0.5, 0.5, 1.0),
    vec4(-0.5, -0.5, -0.5, 1.0),
    vec4(-0.5, 0.5, -0.5, 1.0),
    vec4(0.5, 0.5, -0.5, 1.0),
    vec4(0.5, -0.5, -0.5, 1.0)
  ];
     */
    private static int[] CUBE_INDICES = {
            1, 0, 3,
            2, 2, 3,
            7, 6, 3,
            0, 4, 7,
            6, 5, 1,
            2, 4, 5,
            6, 7, 5,
            4, 0, 1
    };

    /*
-0.5f, 0.5f, 0.5f, 1,
-0.5f, -0.5f, 0.5f, 1,
0.5f, -0.5, 0.5, 1,
-0.5f, 0.5, 0.5, 1,
0.5f, -0.5, 0.5, 1,
0.5f, 0.5, 0.5, 1,
0.5f, 0.5, 0.5, 1,
0.5f, -0.5, 0.5, 1,
0.5f, -0.5, -0.5, 1,
0.5f, 0.5, 0.5, 1,
0.5f, -0.5, -0.5, 1,
0.5f, 0.5, -0.5, 1,
0.5f, -0.5, 0.5, 1,
-0.5f, -0.5, 0.5, 1,
-0.5f, -0.5, -0.5, 1,
0.5f, -0.5, 0.5, 1,
-0.5f, -0.5, -0.5, 1,
0.5f, -0.5, -0.5, 1,
0.5f, 0.5, -0.5, 1,
-0.5f, 0.5, -0.5, 1,
-0.5f, 0.5, 0.5, 1,
0.5f, 0.5, -0.5, 1,
-0.5f, 0.5, 0.5, 1,
0.5f, 0.5, 0.5, 1,
-0.5f, -0.5, -0.5, 1,
-0.5f, 0.5, -0.5, 1,
0.5f, 0.5, -0.5, 1,
-0.5f, -0.5, -0.5, 1,
0.5f, 0.5, -0.5, 1,
0.5f, -0.5, -0.5, 1,
-0.5, 0.5, -0.5, 1,
-0.5, -0.5, -0.5, 1,
-0.5, -0.5, 0.5, 1,
-0.5, 0.5, -0.5, 1,
-0.5, -0.5, 0.5, 1,
-0.5, 0.5, 0.5, 1
     */

    private static float[] C_V = {
            -1.0f,-1.0f,-1.0f, // triangle 1 : begin
            -1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, // triangle 1 : end
            1.0f, 1.0f,-1.0f, // triangle 2 : begin
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f, // triangle 2 : end
            1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f,-1.0f, 1.0f
    };

    private static Vec4[] VERTICES = new Vec4[C_V.length / 3];

    static {
        for (int i = 0; i < VERTICES.length; i++) {
            int a = i * 3;
            int b = i * 3 + 1;
            int c = i * 3 + 2;
            VERTICES[i] = new Vec4(C_V[a] / 2, C_V[b] / 2, C_V[c] / 2, 1);
        }
    }

    public static Vec4[] cube() {
        return VERTICES;
    }
}
