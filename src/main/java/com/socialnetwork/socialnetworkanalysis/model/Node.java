package com.socialnetwork.socialnetworkanalysis.model;

import javafx.scene.paint.Color;

public class Node {
    private String id;
    private String name;
    private double activity;
    private double interaction;
    private double connectionCount;
    private double x, y;
    private Color color;

    // --- YENİ EKLENEN: Layout Hesaplaması İçin Geçici Değişkenler ---
    private double dx; // X eksenindeki değişim
    private double dy; // Y eksenindeki değişim

    public Node(String id, String name, double activity, double interaction, double connectionCount) {
        this.id = id;
        this.name = name;
        this.activity = activity;
        this.interaction = interaction;
        this.connectionCount = connectionCount;
        this.color = Color.web("#00e5ff");
    }

    // Getter & Setter (Eskiler aynı kalıyor)
    public String getId() { return id; }
    public String getName() { return name; }
    public double getActivity() { return activity; }
    public double getInteraction() { return interaction; }
    public double getConnectionCount() { return connectionCount; }
    public double getX() { return x; }
    public double getY() { return y; }
    public Color getColor() { return color; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setColor(Color color) { this.color = color; }
    public void setActivity(double activity) { this.activity = activity; }
    public void setInteraction(double interaction) { this.interaction = interaction; }
    public void setConnectionCount(double connectionCount) { this.connectionCount = connectionCount; }

    // --- YENİ METODLAR ---
    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }
    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }

    @Override public String toString() { return name; }
}