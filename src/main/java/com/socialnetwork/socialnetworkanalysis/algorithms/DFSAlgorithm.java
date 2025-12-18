package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;

import java.util.*;

public class DFSAlgorithm implements GraphAlgorithm {

    @Override
    public String getName() {
        return "DFS (Derinlik Öncelikli Arama)";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- DFS Algoritması Başlatılıyor: " + startNode.getName() + " ---");

        // DFS için Stack (Yığın) kullanılır. En son giren ilk çıkar (LIFO).
        Stack<Node> stack = new Stack<>();
        Set<Node> visited = new HashSet<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            // Eğer daha önce ziyaret etmediysek işle
            if (!visited.contains(current)) {
                visited.add(current);
                System.out.println("Ziyaret Edildi: " + current.getName());

                // Komşuları al
                List<Edge> neighbors = graph.getNeighbors(current);

                // Komşuları tersten ekle ki doğru sırada çıksınlar (İsteğe bağlı görsel detay)
                // Amaç: Stack yapısı olduğu için, en son eklenen ilk işlenir.
                for (Edge edge : neighbors) {
                    Node target = edge.getTarget();
                    if (!visited.contains(target)) {
                        stack.push(target);
                        // System.out.println("  -> Yığına eklendi: " + target.getName());
                    }
                }
            }
        }
        System.out.println("--- DFS Tamamlandı ---");
    }
}