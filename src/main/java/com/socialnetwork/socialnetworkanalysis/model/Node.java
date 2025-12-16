package com.socialnetwork.socialnetworkanalysis.model;

import java.util.Objects;

public class Node {
    private String id;
    private String name;

    // PDF'teki sayısal özellikler
    private double activity;        // Özellik 1 (Aktiflik)
    private double interaction;     // Özellik 2 (Etkileşim)
    private double connectionCount; // Özellik 3 (Bağlantı Sayısı)

    // Ekranda çizim yaparken kullanacağımız koordinatlar
    private double x, y;

    public Node(String id, String name, double activity, double interaction, double connectionCount) {
        this.id = id;
        this.name = name;
        this.activity = activity;
        this.interaction = interaction;
        this.connectionCount = connectionCount;
    }

    // Getter Metotları
    public String getId() { return id; }
    public String getName() { return name; }
    public double getActivity() { return activity; }
    public double getInteraction() { return interaction; }
    public double getConnectionCount() { return connectionCount; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    @Override
    public String toString() { return name + " (" + id + ")"; }

    // HashCode ve Equals (Listelerde düzgün çalışması için şart)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}