package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;

import java.util.*;

public class AStarAlgorithm implements GraphAlgorithm {

    @Override
    public String getName() {
        return "A* (A-Star) En Kısa Yol";
    }

    // A* algoritması bir "HEDEF" düğüm ister.
    // Ancak bizim arayüzümüz (Interface) sadece startNode alıyor.
    // Bu yüzden bu algoritmayı çalıştırmak için Controller'da özel bir metod yazacağız.
    // Buradaki execute metodu standart kalıp için boş durabilir veya varsayılan bir işlem yapabilir.
    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("A* algoritması hedef düğüm gerektirir. Lütfen Controller üzerinden hedef belirterek çalıştırın.");
    }

    // ASIL İŞİ YAPAN METOD (Başlangıç -> Bitiş)
    public void findPath(Graph graph, Node start, Node target) {
        System.out.println("--- A* Algoritması Başlatılıyor: " + start.getName() + " -> " + target.getName() + " ---");

        // Gelen Maliyet (Başlangıçtan buraya kadar ne kadar tuttu?)
        Map<Node, Double> gScore = new HashMap<>();
        // Tahmini Toplam Maliyet (Gelen Maliyet + Hedefe Kalan Kuş Uçuşu Mesafe)
        Map<Node, Double> fScore = new HashMap<>();

        // Yolu geri takip etmek için
        Map<Node, Node> cameFrom = new HashMap<>();

        // Gidilecekler listesi (En düşük fScore'a sahip olan en üstte)
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> fScore.getOrDefault(n, Double.MAX_VALUE)));

        // Başlangıç ayarları
        for (Node node : graph.getAllNodes()) {
            gScore.put(node, Double.MAX_VALUE);
            fScore.put(node, Double.MAX_VALUE);
        }

        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, target));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Hedefe ulaştık mı?
            if (current.equals(target)) {
                printPath(cameFrom, current, gScore.get(target));
                return;
            }

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                double tentativeGScore = gScore.get(current) + edge.getWeight();

                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) + heuristic(neighbor, target));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        System.out.println("Hedefe ulaşılamadı!");
    }

    // Heuristic: Kuş uçuşu mesafe (Öklid)
    private double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    // Yolu yazdıran yardımcı metod
    private void printPath(Map<Node, Node> cameFrom, Node current, double totalCost) {
        List<String> path = new ArrayList<>();
        path.add(current.getName());

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current.getName());
        }
        Collections.reverse(path);

        System.out.println("En Kısa Yol (A*): " + String.join(" -> ", path));
        System.out.println("Toplam Maliyet: " + String.format("%.2f", totalCost));
        System.out.println("----------------------------------------------------");
    }
}