package com.prj.m8eat.model.dto; // 패키지는 실제 경로에 맞게 조정하세요

import java.util.Map;

public class CropBox {
    private int x;
    private int y;
    private int width;
    private int height;

    public CropBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, Integer> toMap() {
        return Map.of(
                "x", x,
                "y", y,
                "width", width,
                "height", height
        );
    }
}
