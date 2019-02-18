package com.bwh.renderer;

import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;

public class Camera {
    private Vec4 position;
    private Vec4 rotation;
    private Mat4 transformation;
    private boolean shouldUpdate;

    public Camera() {
        position = new Vec4(0, 0, 0, 1);
        rotation = new Vec4(0, 0, 0, 0);
        transformation = Mat4.identity();
    }

    private void flag() {
        shouldUpdate = true;
    }

    private void unflag() {
        shouldUpdate = false;
    }

    public void setPosition(Vec4 position) {
        this.position.set(position);
        flag();
    }

    public void setRoll(float roll) {
        rotation.set(2, roll);
        flag();
    }

    public void setYaw(float yaw) {
        rotation.set(1, yaw);
        flag();
    }

    public void setPitch(float pitch) {
        rotation.set(0, pitch);
        flag();
    }

    public Mat4 getTransformation() {
        if (shouldUpdate) {
            // Calculate transformation
            var transMatrix = Mat4.translate(position.get(0), position.get(1), position.get(2));
            var rotMatrix = Mat4.rotateX(2 * Math.PI - rotation.get(0))
                    .mult(Mat4.rotateY(2 * Math.PI - rotation.get(1)))
                    .mult(Mat4.rotateZ(2 * Math.PI - rotation.get(2)));
            var matrix = rotMatrix.mult(transMatrix);
            transformation.set(matrix);
        }
        return transformation;
    }
}
