package com.socialnetwork.socialnetworkanalysis.model;

public class Edge {
    private Node source; // Başlangıç düğümü
    private Node target; // Bitiş düğümü
    private double weight; // Hesaplanan ağırlık

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
        this.weight = calculateWeight(); // Otomatik hesapla
    }

    // PDF Madde 4.3'teki Özel Karekök Formülü
    private double calculateWeight() {
        double diffActivity = source.getActivity() - target.getActivity();
        double diffInteraction = source.getInteraction() - target.getInteraction();
        double diffConnection = source.getConnectionCount() - target.getConnectionCount();

        // 1 + Karekök(...)
        return 1 + Math.sqrt(
                Math.pow(diffActivity, 2) +
                        Math.pow(diffInteraction, 2) +
                        Math.pow(diffConnection, 2)
        );
    }

    public Node getSource() { return source; }
    public Node getTarget() { return target; }
    public double getWeight() { return weight; }
}