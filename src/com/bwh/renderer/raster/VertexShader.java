package com.bwh.renderer.raster;

import com.bwh.renderer.linalg.Vec4;

public interface VertexShader {
    Vec4 transform(int i, Vec4 vertex, float[] dBuffer);
}
