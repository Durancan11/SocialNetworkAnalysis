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

        // 1. Mesafe Tablosu: Her düğüme olan en kısa mesafeyi tutar
        Map<Node, Double> distances = new HashMap<>();

        // 2. Önceki Düğüm Tablosu: Yolu geri izlemek için (Nereden geldik?)
        Map<Node, Node> previousNodes = new HashMap<>();

        // 3. Öncelik Kuyruğu: En kısa mesafeli düğümü hep en öne koyar
        PriorityQueue<NodeDistancePair> queue = new PriorityQueue<>(Comparator.comparingDouble(pair -> pair.distance));

        // Başlangıç ayarları
        for (Node node : graph.getAllNodes()) {
            if (node.equals(startNode)) {
                distances.put(node, 0.0);
                queue.add(new NodeDistancePair(node, 0.0));
            } else {
                distances.put(node, Double.MAX_VALUE); // Diğerleri sonsuz uzaklıkta
            }
        }

        while (!queue.isEmpty()) {
            // Kuyruktan en yakın düğümü çek
            NodeDistancePair currentPair = queue.poll();
            Node current = currentPair.node;

            // Komşuları gez
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                double newDist = distances.get(current) + edge.getWeight();

                // Eğer daha kısa bir yol bulduysak güncelle
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previousNodes.put(neighbor, current);

                    // Kuyruğa yeni mesafesiyle ekle
                    queue.add(new NodeDistancePair(neighbor, newDist));
                }
            }
        }

        // SONUÇLARI YAZDIR
        System.out.println("En Kısa Yollar (" + startNode.getName() + " merkezli):");
        for (Node node : graph.getAllNodes()) {
            if (node != startNode) {
                String path = getPathString(previousNodes, node);
                double dist = distances.get(node);
                // Mesafeyi virgülden sonra 2 hane göster
                System.out.printf("  -> %s'a mesafe: %.2f | Yol: %s%n", node.getName(), dist, path);
            }
        }
        System.out.println("--- Dijkstra Tamamlandı ---");
    }

    // Yolu metin olarak oluşturmak için yardımcı metod (Örn: Ahmet -> Ayşe -> Fatma)
    private String getPathString(Map<Node, Node> previousNodes, Node target) {
        List<String> path = new ArrayList<>();
        for (Node at = target; at != null; at = previousNodes.get(at)) {
            path.add(at.getName());
        }
        Collections.reverse(path);
        return String.join(" -> ", path);
    }

    // Kuyrukta tutmak için yardımcı sınıf (Çift: Düğüm + Mesafe)
    private static class NodeDistancePair {
        Node node;
        double distance;

        public NodeDistancePair(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}