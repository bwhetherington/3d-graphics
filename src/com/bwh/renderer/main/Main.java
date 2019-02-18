package com.bwh.renderer.main;

import com.bwh.renderer.Camera;
import com.bwh.renderer.Mesh;
import com.bwh.renderer.linalg.Mat4;
import com.bwh.renderer.linalg.Vec4;
import com.bwh.renderer.raster.Cube;
import com.bwh.renderer.raster.RasterRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import com.bwh.renderer.color.Color;

public class Main {
    private static final int WIDTH = 640, HEIGHT = 480;
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

        final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
        float yaw = 0;
        float pitch = 0;
        boolean[] keys = new boolean[4];

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        keys[UP] = true;
                        break;
                    case KeyEvent.VK_S:
                        keys[DOWN] = true;
                        break;
                    case KeyEvent.VK_A:
                        keys[LEFT] = true;
                        break;
                    case KeyEvent.VK_D:
                        keys[RIGHT] = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        keys[UP] = false;
                        break;
                    case KeyEvent.VK_S:
                        keys[DOWN] = false;
                        break;
                    case KeyEvent.VK_A:
                        keys[LEFT] = false;
                        break;
                    case KeyEvent.VK_D:
                        keys[RIGHT] = false;
                        break;
                }
            }
        });

        Insets insets = frame.getInsets();
        int x = insets.left;
        int y = insets.top;
        RasterRenderer renderer = new RasterRenderer(x, y, WIDTH, HEIGHT);

        Mat4 proj = Mat4.perspective((float) (Math.PI / 4), ((float) WIDTH) / HEIGHT, 0.001f, 100);

        var eye = new Vec4(0, 2.5f, 0, 0);
        var at = new Vec4(0, 0, 0, 0);
        var up = new Vec4(0, 1, 0, 0);

        var cam = new Camera();
        cam.setPosition(eye);
        var view = cam.getTransformation();

        Mat4 projView = proj.mult(view);

        Mat4 model = Mat4.scale(1, 1, 1);


        renderer.setVertexShader((i, v, dBuffer) -> {
            var camZ = view.mult(model).mult(v).get(2);
            dBuffer[i] = camZ;
            return proj.mult(view).mult(model).mult(v);
        });

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

        List<Mesh> meshes = new ArrayList<>();

        for (int i = -3; i <= 3; i++) {
            for (int j = -3; j <= 3; j++) {
                var mesh = new Mesh(cubeV, cubeC);
                mesh.setPosition(i, 0, j);
                meshes.add(mesh);
            }

        }

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

                if (keys[LEFT]) {
                    yaw += 0.015;
                }
                if (keys[RIGHT]) {
                    yaw -= 0.015;
                }
                if (keys[UP]) {
                    pitch += 0.015;
                }
                if (keys[DOWN]) {
                    pitch -= 0.015;
                }

                cam.setYaw(yaw);
                cam.setPitch(pitch);
                view.set(cam.getTransformation());

                var in = 0;
                for (var mesh : meshes) {
                    switch (in % 3) {
                        case 0:
                            mesh.setYaw((float) i);
                            break;
                        case 1:
                            mesh.setRoll((float) i);
                            break;
                        case 2:
                            mesh.setPitch((float) i);
                            break;
                    }
                    mesh.draw(model, renderer);
                    in++;
                }

                draw(strategy, renderer);

                i += 0.01 * ratio;

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
