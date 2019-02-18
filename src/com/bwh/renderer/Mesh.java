package com.bwh.renderer;

import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;
import com.bwh.renderer.raster.RasterRenderer;

public class Mesh {
    private Vec4[] vertices;
    private Vec4[] colors;

    private Vec4 position = new Vec4(0, 0, 0, 1);
    private Vec4 scale = new Vec4(1, 1, 1, 1);
    private Vec4 rotation = new Vec4(0, 0, 0, 1);

    private Mat4 transformation;

    private boolean flag;

    public Mesh(Vec4[] vertices, Vec4[] colors) {
        this.vertices = vertices;
        this.colors = colors;
    }

    private void flag() {
        this.flag = true;
    }

    private void unflag() {
        this.flag = false;
    }

    public void setPosition(float x, float y, float z) {
        position.set(0, x);
        position.set(1, y);
        position.set(2, z);
        flag();
    }

    public void setScale(float x, float y, float z) {
        scale.set(0, x);
        scale.set(1, y);
        scale.set(2, z);
        flag();
    }

    public void setPitch(float theta) {
        rotation.set(0, theta);
        flag();
    }

    public void setRoll(float theta) {
        rotation.set(1, theta);
        flag();
    }

    public void setYaw(float theta) {
        rotation.set(2, theta);
        flag();
    }

    public Mat4 getTransformation() {
        if (flag) {
            // Create rotation
            Mat4 rot = Mat4.rotateX(rotation.get(2))
                    .mult(Mat4.rotateY(rotation.get(1)))
                    .mult(Mat4.rotateZ(rotation.get(0)));
            float tx = position.get(0);
            float ty = position.get(1);
            float tz = position.get(2);
            Mat4 translate = Mat4.translate(tx, ty, tz);

            float sx = scale.get(0);
            float sy = scale.get(1);
            float sz = scale.get(2);
            Mat4 scale = Mat4.scale(sx, sy, sz);

            transformation = translate.mult(scale).mult(rot);

            unflag();
        }

        return transformation;
    }

    public void draw(Mat4 model, RasterRenderer renderer) {
        model.set(getTransformation());
        renderer.setVertices(vertices);
        renderer.setColors(colors);
        renderer.drawArrays();
    }
}
