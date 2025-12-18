package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;

import java.util.*;

public class ConnectedComponentsAlgorithm implements GraphAlgorithm {

    @Override
    public String getName() {
        return "Bağlı Bileşenler (Topluluk Bulma)";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- Topluluk Analizi (Connected Components) Başlıyor ---");

        Set<Node> visited = new HashSet<>();
        List<List<Node>> components = new ArrayList<>();

        // Tüm düğümleri gez
        for (Node node : graph.getAllNodes()) {
            if (!visited.contains(node)) {
                // Eğer bu düğüme daha önce gelmediysek, yeni bir topluluk bulduk demektir!
                List<Node> newComponent = new ArrayList<>();
                // Bu topluluğun içine gir ve herkesi bul (BFS kullanarak)
                bfsForComponent(graph, node, visited, newComponent);
                components.add(newComponent);
            }
        }

        // Raporlama
        System.out.println("Toplam " + components.size() + " adet ayrık topluluk bulundu.");
        for (int i = 0; i < components.size(); i++) {
            System.out.println("TOPLULUK #" + (i + 1) + ": " + components.get(i));
        }
        System.out.println("--------------------------------------------------------");
    }

    // Yardımcı BFS Metodu: Bir düğümden başlayıp gidebildiği herkesi toplar
    private void bfsForComponent(Graph graph, Node start, Set<Node> visited, List<Node> component) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        component.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    component.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
}