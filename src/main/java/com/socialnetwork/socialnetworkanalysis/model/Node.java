package com.socialnetwork.socialnetworkanalysis.model;

import javafx.scene.paint.Color;

public class Node {
    private String id;
    private String name;
    private double activity;
    private double interaction;
    private double connectionCount;

    // Koordinatlar
    private double x;
    private double y;

    // Görsel Renk (Renklendirme algoritması için)
    private Color color;

    public Node(String id, String name, double activity, double interaction, double connectionCount) {
        this.id = id;
        this.name = name;
        this.activity = activity;
        this.interaction = interaction;
        this.connectionCount = connectionCount;
        this.color = Color.web("#00e5ff"); // Varsayılan Neon Mavi
    }

    // --- GETTER METODLARI (Okuma) ---
    public String getId() { return id; }
    public String getName() { return name; }
    public double getActivity() { return activity; }
    public double getInteraction() { return interaction; }
    public double getConnectionCount() { return connectionCount; }
    public double getX() { return x; }
    public double getY() { return y; }
    public Color getColor() { return color; }

    // --- SETTER METODLARI (Yazma - EKSİK OLAN KISIM BURASIYDI) ---

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setColor(Color color) { this.color = color; }

    // Hata veren metodlar bunlardı, şimdi ekliyoruz:
    public void setActivity(double activity) {
        this.activity = activity;
    }

    public void setInteraction(double interaction) {
        this.interaction = interaction;
    }

    public void setConnectionCount(double connectionCount) {
        this.connectionCount = connectionCount;
    }

    @Override
    public String toString() {
        return name;
    }
}