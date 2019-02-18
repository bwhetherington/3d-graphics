package com.bwh.renderer.color;

public final class Color {
    private Color() {}

    private static int COLOR_MASK = 0x000000FF;

    public static int rgba(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    public static int alpha(int c) {
        return (c >> 24) & COLOR_MASK;
    }

    public static int red(int c) {
        return (c >> 16) & COLOR_MASK;
    }

    public static int green(int c) {
        return (c >> 8) & COLOR_MASK;
    }

    public static int blue(int c) {
        return c & COLOR_MASK;
    }

//    public static int add(int c1, int c2) {
//        final int a1 = alpha(c1);
//        final int r1 = red(c1);
//        final int g1 = green(c1);
//        final int b1 = blue(c1);
//
//        final int a2 = alpha(c2);
//        final int r2 = red(c2);
//        final int g2 = green(c2);
//        final int b2 = blue(c2);
//
//        final float aDiff = (a2 - a1) / 255.0f;
//
//    }
}
