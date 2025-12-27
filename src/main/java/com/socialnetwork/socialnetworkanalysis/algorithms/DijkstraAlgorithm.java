package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;

import java.util.*;

public class DijkstraAlgorithm implements GraphAlgorithm {

    @Override
    public String getName() {
        return "Dijkstra (En Kısa Yol)";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- Dijkstra Algoritması Başlatılıyor: " + startNode.getName() + " ---");

        // 1. Mesafe Tablosu
        Map<Node, Double> distances = new HashMap<>();

        // 2. Önceki Düğüm Tablosu
        Map<Node, Node> previousNodes = new HashMap<>();

        // 3. Öncelik Kuyruğu
        PriorityQueue<NodeDistancePair> queue = new PriorityQueue<>(Comparator.comparingDouble(pair -> pair.distance));

        // Başlangıç ayarları
        for (Node node : graph.getAllNodes()) {
            if (node.equals(startNode)) {
                distances.put(node, 0.0);
                queue.add(new NodeDistancePair(node, 0.0));
            } else {
                distances.put(node, Double.MAX_VALUE); // Sonsuz
            }
        }

        while (!queue.isEmpty()) {
            NodeDistancePair currentPair = queue.poll();
            Node current = currentPair.node;

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                double newDist = distances.get(current) + edge.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);
                    queue.add(new NodeDistancePair(neighbor, newDist));
                }
            }
        }

        // --- GÖRSEL DÜZELTME YAPILMIŞ SONUÇ YAZDIRMA ---
        System.out.println("En Kısa Yollar (" + startNode.getName() + " merkezli):");
        for (Node node : graph.getAllNodes()) {
            if (node.equals(startNode)) continue;

            double dist = distances.get(node);

            // EĞER MESAFE HALA SONSUZSA, YOL BULUNAMAMIŞ DEMEKTİR
            if (dist == Double.MAX_VALUE || dist == Double.POSITIVE_INFINITY) {
                System.out.println("  -> " + node.getName() + ": ULAŞILAMAZ (Yol Yok)");
            } else {
                String path = getPathString(previousNodes, node);
                System.out.printf("  -> %s'a mesafe: %.2f | Yol: %s%n", node.getName(), dist, path);
            }
        }
        System.out.println("--- Dijkstra Tamamlandı ---");
    }

    // Yolu metin olarak oluşturmak için yardımcı metod
    private String getPathString(Map<Node, Node> previousNodes, Node target) {
        List<String> path = new ArrayList<>();
        for (Node at = target; at != null; at = previousNodes.get(at)) {
            path.add(at.getName());
        }
        Collections.reverse(path);
        return String.join(" -> ", path);
    }

    // Yardımcı Sınıf
    private static class NodeDistancePair {
        Node node;
        double distance;

        public NodeDistancePair(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}