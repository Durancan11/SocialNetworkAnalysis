package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;

import java.util.HashMap;
import java.util.Map;

public class DegreeCentralityAlgorithm implements GraphAlgorithm {
    @Override
    public String getName() {
        return "Derece Merkeziliği (Degree Centrality)";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- Derece Merkeziliği (Popülerlik) Hesaplanıyor ---");

        Map<Node, Integer> scores = new HashMap<>();

        // Her düğümün kaç arkadaşı var? (Derecesini hesapla)
        for (Node node : graph.getAllNodes()) {
            int degree = graph.getNeighbors(node).size();
            scores.put(node, degree);
        }

        // Sonuçları SIRALI yazdır (En popüler en üstte)
        System.out.println("SONUÇLAR (Büyükten Küçüğe):");
        scores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Büyükten küçüğe sıralama
                .forEach(entry -> {
                    System.out.println("  -> " + entry.getKey().getName() + " | Arkadaş Sayısı: " + entry.getValue());
                });

        System.out.println("----------------------------------------------------");
    }
}