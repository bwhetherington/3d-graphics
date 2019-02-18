package com.bwh.renderer.main;

import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;
import com.bwh.renderer.raster.Cube;
import com.bwh.renderer.raster.RasterRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

import com.bwh.renderer.color.Color;

public class Main {
    private static final int WIDTH = 640, HEIGHT = 640;
//    public static void main(String[] args) {
//        Mat4 scale = Mat4.scale(2, 2, 0.5f);
//        Mat4 trans = Mat4.translate(10, 12, 14);
//
//        // Scale then transform
//        Mat4 transform = scale.mult(trans);
//
//        Vec4 point = new Vec4(1, 2, 3, 1);
//        Vec4 translated = transform.mult(point);
//        System.out.println(translated);
//    }

    private static void draw(BufferStrategy strategy, RasterRenderer renderer) {
        do {
            do {
                Graphics graphics = strategy.getDrawGraphics();
                renderer.render(graphics);
                graphics.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
        } while (strategy.contentsLost());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");

        Container content = frame.getContentPane();
        content.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.pack();

        Insets insets = frame.getInsets();
        int x = insets.left;
        int y = insets.top;
        RasterRenderer renderer = new RasterRenderer(x, y, WIDTH, HEIGHT);

        Mat4 proj = Mat4.perspective((float) (Math.PI / 5), ((float) WIDTH) / HEIGHT, 0.1f, 100);

        var eye = new Vec4(0, 2, 3, 0);
        var at = new Vec4(0, 0, 0, 0);
        var up = new Vec4(0, 1, 0, 0);
        Mat4 view = Mat4.lookAt(eye, at, up);


        Mat4 projView = proj.mult(view);

        Mat4 model = Mat4.scale(1, 1, 1);


        renderer.setVertexShader((i, v, dBuffer) -> {
            var camZ = view.mult(model).mult(v).get(2);
            dBuffer[i] = camZ;
            return proj.mult(view).mult(model).mult(v);
        });

        Vec4[] verts = new Vec4[]{
                new Vec4(-0.5f, -0.5f, 0, 1), // should be 0 0
                new Vec4(0.5f, -0.5f, 0, 1),  // should be 400 0
                new Vec4(0, 0.5f, 0, 1)    // should be 200 400
        };
        Vec4[] colors = new Vec4[]{
                new Vec4(1, 0, 0, 0),
                new Vec4(0, 1, 0, 0),
                new Vec4(0, 0, 1, 0)
        };

        Vec4[] cubeV = Cube.cube();
        Vec4[] cubeC = new Vec4[cubeV.length];
        for (int i = 0; i < cubeV.length; i++) {
            int ci = i % 3;
            Vec4 color = colors[ci];
            cubeC[i] = color;
        }

        renderer.setVertices(cubeV);
        renderer.setColors(cubeC);
        renderer.drawArrays();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.createBufferStrategy(2);
        BufferStrategy strategy = frame.getBufferStrategy();

        double i = 0;
        double r = 100;
        double cx = 200, cy = 200;
        int clear = Color.rgb(0, 0, 0);
        try {
            long target = 16;
            long start = System.nanoTime(), stop, dt = 0;
            while (true) {
                Thread.sleep(Math.max(0, target - dt));
                double ratio = ((double) dt) / target;

                renderer.clear(clear);

//                model.set(Mat4.rotateX((float) i));
//                view.set(Mat4.lookAt(new Vec4(0f, (float) Math.sin(i) + 2, 3f, 0f), at, up));
                model.set(Mat4.rotateZ((float) i));
                renderer.drawArrays();
                model.set(Mat4.translate(
                        (float) (-0.2f * Math.sin(i)),
                        (float) (-0.2f * Math.cos(i)),
                        (float) (0.2f * Math.cos(i)))
                        .mult(Mat4.rotateX((float) i)));


//                model.set(Mat4.rotateY((float) i));
                renderer.drawArrays();
                draw(strategy, renderer);

                i += 0.02 * ratio;

//                component.repaint();
                stop = System.nanoTime();
                dt = (stop - start) / 1000000;
                start = System.nanoTime();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
