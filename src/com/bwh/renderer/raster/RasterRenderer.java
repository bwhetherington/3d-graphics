package com.bwh.renderer.raster;

import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;

import com.bwh.renderer.color.Color;

public class RasterRenderer {
    private BufferedImage image;
    private int width;
    private int height;
    private int x;
    private int y;

    private int color = Color.rgb(255, 255, 255);

    private Vec4[] vertices;
    private Vec4[] colors;
    private VertexShader shader;

    private Mat4 viewport;
    private float[] depthBuffer;
    private float[][] bccBuffer;

    public RasterRenderer(int x, int y, int w, int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;

        Mat4 transToTopLeft = Mat4.translate(1, 1, 0);
        Mat4 scaleToPort = Mat4.scale(w / 2, h / 2, 1);

        viewport = scaleToPort.mult(transToTopLeft);
        depthBuffer = new float[width * height];
        bccBuffer = new float[width * height][3];

        for (int i = 0; i < width * height; i++) {
            depthBuffer[i] = Float.POSITIVE_INFINITY;
        }
    }

    public RasterRenderer(int w, int h) {
        this(0, 0, w, h);
    }

    private synchronized float getDepthBuffer(int i) {
        return depthBuffer[i];
    }

    private synchronized void setDepthBuffer(int i, float depth) {
        depthBuffer[i] = depth;
    }

    private synchronized void setBcc(int i, float u, float v, float w) {
        float[] bcc = bccBuffer[i];
        bcc[0] = u;
        bcc[1] = v;
        bcc[2] = w;
    }

    public void render(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public void clear(int c) {
        drawRect(0, 0, width, height, c);
        for (int i = 0; i < width * height; i++) {
            depthBuffer[i] = Float.POSITIVE_INFINITY;
        }
    }

    public void drawRect(int x, int y, int w, int h, int c) {
        int minX = Math.max(0, x);
        int minY = Math.max(0, y);
        int maxX = Math.min(width, x + w);
        int maxY = Math.min(height, y + h);
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                image.setRGB(i, j, c);
            }
        }
    }

    private void plot(int x, int y, int c) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            image.setRGB(x, y, c);
        }
    }

    public void drawLine(double x0, double y0, double z0, double x1, double y1, double z1, Vec4 c0, Vec4 c1) {
        double error = 0;

        double slope = Math.abs((y1 - y0) / (x1 - x0));
        if (slope > 1) {
            // Render steep line
            slope = 1 / slope;

            // Check order of coords
            if (y1 < y0) {
                double tx = x0;
                double ty = y0;
                x0 = x1;
                y0 = y1;
                x1 = tx;
                y1 = ty;
            }

            int xDir = (int) Math.signum(x1 - x0);
            int x = (int) x0;

            for (int y = (int) y0; y < (int) y1; y++) {
                float u = (float) (y / y1);
                float v = 1 - u;

                Vec4 c0p = c0.mult(v);
                Vec4 c1p = c1.mult(v);
                Vec4 c = c0p.add(c1p);

                int cr = (int) (c.get(0) * 255);
                int cg = (int) (c.get(1) * 255);
                int cb = (int) (c.get(2) * 255);

                int color = Color.rgb(cr, cg, cb);

                float depth = (float) (u * z0 + v * z1);

                int depthIndex = y * width + x;

                if (depth < depthBuffer[depthIndex]) {
                    depthBuffer[depthIndex] = depth;
                    plot(x, y, color);
                }

                error += slope;
                if (error >= 0.5) {
                    x += xDir;
                    error -= 1;
                }
            }
        } else {
            // Render shallow line
//            slope = 1 / slope;

            // Check order of coords
            if (x1 < x0) {
                double tx = x0;
                double ty = y0;
                x0 = x1;
                y0 = y1;
                x1 = tx;
                y1 = ty;
            }

            int yDir = (int) Math.signum(y1 - y0);
            int y = (int) y0;

            for (int x = (int) x0; x < (int) x1; x++) {
                float u = (float) (x / x1);
                float v = 1 - u;

                Vec4 c0p = c0.mult(u);
                Vec4 c1p = c1.mult(v);
                Vec4 c = c0p.add(c1p);

                int cr = (int) (c.get(0) * 255);
                int cg = (int) (c.get(1) * 255);
                int cb = (int) (c.get(2) * 255);

                int color = Color.rgb(cr, cg, cb);

                float depth = (float) (u * z0 + v * z1);

                int depthIndex = y * width + x;

                if (depth < depthBuffer[depthIndex]) {
                    depthBuffer[depthIndex] = depth;
                    plot(x, y, color);
                }
                error += slope;
                if (error >= 0.5) {
                    y += yDir;
                    error -= 1;
                }
            }
        }

    }

    public void setVertices(Vec4[] vertices) {
        this.vertices = vertices;
    }

    public void setVertexShader(VertexShader shader) {
        this.shader = shader;
    }

//    void drawLine(Vec4 p0, Vec4 p1) {
//
//        int x0 = (int) p0.get(0);
//        int y0 = (int) p0.get(1);
//        int x1 = (int) p1.get(0);
//        int y1 = (int) p1.get(1);
//
//        drawLine(x0, y0, x1, y1);
//    }

    public void setColors(Vec4[] colors) {
        this.colors = colors;
    }

    private boolean inBounds(float x, float y, float z) {
        return x < 0 || x >= width || y < 0 || y >= height || z < 0;
    }

    public void drawArrays() {
//        Vec4[] transformed = new Vec4[vertices.length];

        if (vertices.length > 0) {
            final List<Vec4> transformed = IntStream
                    .range(0, vertices.length)
                    .parallel()
                    .mapToObj(i -> {
                        var v = vertices[i];
                        var tv = shader.transform(i, v, depthBuffer);
                        var p = viewport.mult(tv);
                        return p;
                    })
                    .map(v -> {
                        var w = v.get(3);
                        var newV = v.mult(1 / w);
                        newV.set(2, w);
                        return newV;
                    })
                    .collect(Collectors.toList());

            IntStream.range(0, transformed.size() / 3)
                    .parallel()
                    .forEach(i -> {
                        var ui = i * 3;
                        var vi = ui + 1;
                        var wi = ui + 2;

                        Vec4 p0 = transformed.get(ui);
                        Vec4 p1 = transformed.get(vi);
                        Vec4 p2 = transformed.get(wi);

                        var x0 = p0.get(0);
                        var y0 = p0.get(1);
                        var z0 = p0.get(2);
                        var x1 = p1.get(0);
                        var y1 = p1.get(1);
                        var z1 = p1.get(2);
                        var x2 = p2.get(0);
                        var y2 = p2.get(1);
                        var z2 = p2.get(2);

//                        System.out.println("POINT");
//                        System.out.println(p0);
//                        System.out.println(p1);
//                        System.out.println(p2);

                        if (inBounds(x0, y0, z0) && inBounds(x1, y1, z1) && inBounds(x2, y2, z2)) {
                            return;
                        }

                        // Calculate normal
                        var ux = x1 - x0;
                        var uy = y1 - y0;
                        var uz = z1 - z0;
                        var vx = x2 - x0;
                        var vy = y2 - y0;
                        var vz = z2 - z0;

                        var nz = Vec4.crossZ(ux, uy, uz, vx, vy, vz);

                        if (nz > 0) {


                            var c0 = colors[i];
                            var c1 = colors[i + 1];
                            var c2 = colors[i + 2];

                            drawTriangle(
                                    x0, y0, z0,
                                    x1, y1, z1,
                                    x2, y2, z2,
                                    c0, c1, c2
                            );

//                            drawLine(x0, y0, z0, x1, y1, z1, c0, c1);
//                            drawLine(x1, y1, z1, x2, y2, z2, c1, c2);
//                            drawLine(x2, y2, z2, x0, y0, z0, c2, c0);
                        }
                    });
        }
    }

    private void bcc(
            float ax, float ay,
            float bx, float by,
            float cx, float cy,
            float px, float py,
            int i
    ) {
        float v0x = bx - ax;
        float v0y = by - ay;
        float v1x = cx - ax;
        float v1y = cy - ay;
        float v2x = px - ax;
        float v2y = py - ay;

        float d00 = v0x * v0x + v0y * v0y;
        float d01 = v0x * v1x + v0y * v1y;
        float d11 = v1x * v1x + v1y * v1y;
        float d20 = v2x * v0x + v2y * v0y;
        float d21 = v2x * v1x + v2y * v1y;
        float denom = d00 * d11 - d01 * d01;
        float v = (d11 * d20 - d01 * d21) / denom;
        float w = (d00 * d21 - d01 * d20) / denom;
        float u = 1 - v - w;

        float[] bcc = bccBuffer[i];
        bcc[0] = u;
        bcc[1] = v;
        bcc[2] = w;
    }

    public boolean inBounds(int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

    private float clamp(float val, float min, float max) {
        return Math.min(Math.max(min, val), max);
    }

    public void drawTriangle(
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            Vec4 c0, Vec4 c1, Vec4 c2
    ) {
        int minX = (int) clamp(Math.min(Math.min(x0, x1), x2), 0, width);
        int maxX = (int) clamp(Math.max(Math.max(x0, x1), x2), 0, width);
        int minY = (int) clamp(Math.min(Math.min(y0, y1), y2), 0, height);
        int maxY = (int) clamp(Math.max(Math.max(y0, y1), y2), 0, height);

        int dx = maxX - minX;
        int dy = maxY - minY;

        IntStream.range(0, dx * dy)
                .parallel()
                .map(i -> i % dx + minX + (i / dx + minY) * width)
                .filter(i -> 0 <= i && i < width * height)
                .forEach(i -> {
                    int x = i % width;
                    int y = i / width;

                    bcc(x0, y0, x1, y1, x2, y2, (float) x, (float) y, i);
                    float[] bcc = bccBuffer[i];

                    float u = bcc[0];
                    float v = bcc[1];
                    float w = bcc[2];

                    boolean inTriangle =
                            0 <= u && u <= 1 &&
                            0 <= v && v <= 1 &&
                            0 <= w && w <= 1;
                    if (inTriangle) {
                        // Calculate color
                        Vec4 c0p = c0.mult(u);
                        Vec4 c1p = c1.mult(v);
                        Vec4 c2p = c2.mult(w);
                        Vec4 color = c0p.add(c1p).add(c2p);

                        int cr = (int) (color.get(0) * 255);
                        int cg = (int) (color.get(1) * 255);
                        int cb = (int) (color.get(2) * 255);

                        float depth = z0 * u + z1 * v + z2 * w;

                        if (depth > 0 && inBounds(x, y) && depth < depthBuffer[i]) {
                            depthBuffer[i] = depth;
                            plot(x, y, Color.rgb(cr, cg, cb));
                        }
                    }
                });

//        for (int x = minX; x < maxX; x++) {
//            for (int y = minY; y < maxY; y++) {
//
//
//            }
//        }
    }
}
