package com.socialnetwork.socialnetworkanalysis.model;

public class Edge {
    private Node source;
    private Node target;
    private double weight; // YENİ: Ağırlık özelliği

    // Yeni Constructor: Ağırlık alıyor
    public Edge(Node source, Node target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    // Getter Metodları
    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public double getWeight() { return weight; }

    // Setter (Gerekirse ağırlığı sonradan değiştirmek için)
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return source.getName() + " -> " + target.getName() + " (" + weight + ")";
    }
}