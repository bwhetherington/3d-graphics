package com.bwh.renderer.linalg;

public class Vec4 {
    private float[] ptr;

    Vec4(float[] ptr) {
        this.ptr = ptr;
    }

    public Vec4(float a, float b, float c, float d) {
        this(new float[]{a, b, c, d});
    }

    public float get(int i) {
        return ptr[i];
    }

    public Vec4 add(Vec4 other) {
        final float[] sum = new float[4];
        for (int i = 0; i < 4; i++) {
            sum[i] = ptr[i] + other.ptr[i];
        }
        return new Vec4(sum);
    }

    public Vec4 subtract(Vec4 other) {
        final float[] diff = new float[4];
        for (int i = 0; i < 4; i++) {
            diff[i] = ptr[i] - other.ptr[i];
        }
        return new Vec4(diff);
    }

    public Vec4 cross(Vec4 other) {
        float[] u = ptr;
        float[] v = other.ptr;
        return new Vec4(
                u[1]*v[2] - u[2]*v[1],
                u[2]*v[0] - u[0]*v[2],
                u[0]*v[1] - u[1]*v[0],
                1
        );
    }

    public static float crossZ(
            float x0, float y0, float z0,
            float x1, float y1, float z1
    ) {
        return x0 * y1 - y0 * x1;
    }

    public Vec4 mult(float scalar) {
        final float[] product = new float[4];
        for (int i = 0; i < 4; i++) {
            product[i] = ptr[i] * scalar;
        }
        return new Vec4(product);
    }

    public float dot(Vec4 other) {
        float sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += ptr[i] * other.ptr[i];
        }
        return sum;
    }

    public float norm(boolean ignoreLast) {
        final int last = ignoreLast ? 3 : 4;
        float sum = 0;
        for (int i = 0; i < last; i++) {
            sum += ptr[i] * ptr[i];
        }
        return (float) Math.sqrt(sum);
    }

    public float norm() {
        return norm(true);
    }

    public Vec4 normalize(boolean ignoreLast) {
        if (!ignoreLast) {
            return mult(1 / norm(false));
        } else {
            float norm = norm(true);
            return new Vec4(
                    ptr[0] / norm,
                    ptr[1] / norm,
                    ptr[2] / norm,
                    ptr[3]
            );
        }
    }

    public Vec4 normalize() {
        return normalize(true);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 4; row++) {
            sb.append(get(row));
            sb.append('\n');
        }
        return sb.toString();
    }

    public void set(int i, float v) {
        ptr[i] = v;
    }

    public void set(Vec4 other) {
        for (int i = 0; i < 4; i++) {
            ptr[i] = other.ptr[i];
        }
    }
}
