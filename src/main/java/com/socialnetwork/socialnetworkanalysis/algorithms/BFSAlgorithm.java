package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;

import java.util.*;

public class BFSAlgorithm implements GraphAlgorithm {

    @Override
    public String getName() {
        return "BFS (Sığ Öncelikli Arama)";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- BFS Algoritması Başlatılıyor: " + startNode.getName() + " ---");

        // 1. Ziyaret edilecekler kuyruğu (Sırada kim var?)
        Queue<Node> queue = new LinkedList<>();

        // 2. Ziyaret edilenler listesi (Tekrar aynı yere gitmemek için)
        Set<Node> visited = new HashSet<>();

        // Başlangıç düğümünü ekle
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            // Kuyruğun başındaki düğümü al
            Node current = queue.poll();
            System.out.println("Ziyaret Edildi: " + current.getName());

            // Bu düğümün komşularını bul
            List<Edge> neighbors = graph.getNeighbors(current);

            for (Edge edge : neighbors) {
                Node target = edge.getTarget();

                // Eğer bu komşuya daha önce gitmediysek kuyruğa ekle
                if (!visited.contains(target)) {
                    visited.add(target);
                    queue.add(target);
                    System.out.println("  -> Kuyruğa eklendi: " + target.getName());
                }
            }
        }
        System.out.println("--- BFS Tamamlandı ---");
    }
}