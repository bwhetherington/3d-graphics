package com.bwh.renderer.linalg;

public class Mat4 {
    private float[] ptr;

    Mat4(float[] ptr) {
        this.ptr = ptr;
    }

    public Mat4(
            float a1, float a2, float a3, float a4,
            float b1, float b2, float b3, float b4,
            float c1, float c2, float c3, float c4,
            float d1, float d2, float d3, float d4
    ) {
        this(new float[]{
                a1, a2, a3, a4,
                b1, b2, b3, b4,
                c1, c2, c3, c4,
                d1, d2, d3, d4
        });
    }

    public Mat4() {
        this(new float[16]);
    }

    public float get(int i) {
        return ptr[i];
    }

    public float get(int row, int col) {
        return ptr[row * 4 + col];
    }

    public float set(int row, int col, float value) {
        int i = row * 4 + col;
        float old = ptr[i];
        ptr[i] = value;
        return old;
    }

    public Mat4 mult(Mat4 other) {
        final float[] prod = new float[16];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += get(i, k) * other.get(k, j);
                }
                prod[i * 4 + j] = sum;
            }
        }

        return new Mat4(prod);
    }

    public Vec4 mult(Vec4 other) {
        final float[] prod = new float[4];
        // row

        for (int row = 0; row < 4; row++) {
            float sum = 0;
            for (int i = 0; i < 4; i++) {
                sum += get(row, i) * other.get(i);
            }
            prod[row] = sum;
        }

        return new Vec4(prod);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                   sb.append(get(row, col));
                   sb.append('\t');
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    public static Mat4 scale(float x, float y, float z) {
        return new Mat4(
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
        );
    }

    public static Mat4 translate(float x, float y, float z) {
        return new Mat4(
                1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1
        );
    }

    public static Mat4 rotateX(float theta) {
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        return new Mat4(
                1, 0, 0, 0,
                0, c, -s, 0,
                0, s, c, 0,
                0, 0, 0, 1
        );
    }

    public static Mat4 rotateX(double theta) {
        return rotateX((float) theta);
    }

    public static Mat4 rotateY(float theta) {
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        return new Mat4(
                c, 0, s, 0,
                0, 1, 0, 0,
                -s, 0, c, 0,
                0, 0, 0, 1
        );
    }

    public static Mat4 rotateY(double theta) {
        return rotateY((float) theta);
    }

    public static Mat4 rotateZ(float theta) {
        final float c = (float) Math.cos(theta);
        final float s = (float) Math.sin(theta);
        return new Mat4(
                c, -s, 0, 0,
                s, c, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    public static Mat4 rotateZ(double theta) {
        return rotateZ((float) theta);
    }

    public static Mat4 perspective(float fovY, float aspect, float near, float far) {
        final Mat4 mat = new Mat4();
        final float f = (float) (1 / Math.tan(fovY / 2));
        final float d = far - near;

        mat.set(0, 0, f / aspect);
        mat.set(1, 1, f);
        mat.set(2, 2, -(near + far) / d);
        mat.set(2, 3, -2 * near * far / d);
        mat.set(3, 2, -1);
        mat.set(3, 3, 0);

        return mat;
    }

    public static Mat4 identity() {
        return new Mat4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }

    public static Mat4 lookAt(Vec4 eye, Vec4 at, Vec4 up) {
        /*
        var v = normalize( subtract(at, eye) );  // view direction vector
    var n = normalize( cross(v, up) );       // perpendicular vector
    var u = normalize( cross(n, v) );        // "new" up vector
         */
        if (eye.equals(at)) {
            return identity();
        }

        Vec4 v = at.subtract(eye).normalize();
        Vec4 n = v.cross(up).normalize();
        Vec4 u = n.cross(v).normalize();
        v = v.mult(-1);

        /*
        var result = mat4(
        vec4( n, -dot(n, eye) ),
        vec4( u, -dot(u, eye) ),
        vec4( v, -dot(v, eye) ),
        vec4()
    );
         */
        float nDot = -n.dot(eye);
        float uDot = -u.dot(eye);
        float vDot = -v.dot(eye);

        return new Mat4(
                n.get(0), n.get(1), n.get(2), nDot,
                u.get(0), u.get(1), u.get(2), uDot,
                v.get(0), v.get(1), v.get(2), vDot,
                0, 0, 0, 1
        );
    }

    public void set(Mat4 other) {
        System.arraycopy(other.ptr, 0, ptr, 0, 16);
    }
}
