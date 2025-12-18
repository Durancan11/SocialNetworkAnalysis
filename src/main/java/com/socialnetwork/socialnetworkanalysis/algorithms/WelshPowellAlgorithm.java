package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class WelshPowellAlgorithm implements GraphAlgorithm {

    // Kullanılacak renk paleti (Sırayla bunları deneyecek)
    private final Color[] palette = {
            Color.RED, Color.GREEN, Color.ORANGE, Color.PURPLE,
            Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BROWN, Color.PINK
    };

    @Override
    public String getName() {
        return "Welsh-Powell Renklendirme";
    }

    @Override
    public void execute(Graph graph, Node startNode) {
        System.out.println("--- Renklendirme Başlıyor ---");

        // 1. Düğümleri derecelerine (bağlantı sayılarına) göre BÜYÜKTEN KÜÇÜĞE sırala
        List<Node> sortedNodes = graph.getAllNodes().stream()
                .sorted((n1, n2) -> Integer.compare(
                        graph.getNeighbors(n2).size(),
                        graph.getNeighbors(n1).size()))
                .collect(Collectors.toList());

        // Renk atamalarını tutmak için (Düğüm -> Renk Indexi)
        Map<Node, Integer> nodeColors = new HashMap<>();

        int colorIndex = 0;

        // Tüm düğümler boyanana kadar devam et
        while (!sortedNodes.isEmpty()) {
            // Paletten bir renk seç (Renkler biterse başa dönmemek için mod almıyoruz, rastgele üretebiliriz ama şimdilik palet yeter)
            Color currentColor = (colorIndex < palette.length) ? palette[colorIndex] : Color.GRAY;

            // Bu turda boyayacağımız düğümler listesi
            List<Node> toColor = new ArrayList<>();

            // Listede kalan en yüksek dereceli düğümü al
            Node head = sortedNodes.get(0);
            toColor.add(head);

            // Listede kalan diğer düğümlere bak
            for (int i = 1; i < sortedNodes.size(); i++) {
                Node candidate = sortedNodes.get(i);

                // Eğer bu aday, şu an boyadıklarımızdan HİÇBİRİNE komşu değilse, onu da aynı renge boyayabiliriz
                boolean isAdjacent = false;
                for (Node colored : toColor) {
                    if (areConnected(graph, candidate, colored)) {
                        isAdjacent = true;
                        break;
                    }
                }

                if (!isAdjacent) {
                    toColor.add(candidate);
                }
            }

            // Seçilenleri boya ve listeden çıkar
            for (Node n : toColor) {
                n.setColor(currentColor); // Düğümün rengini güncelle
                System.out.println("  -> " + n.getName() + " boyandı: " + currentColor.toString());
                sortedNodes.remove(n);
            }

            colorIndex++;
        }
        System.out.println("--- Renklendirme Tamamlandı ---");
    }

    // İki düğüm birbirine bağlı mı?
    private boolean areConnected(Graph graph, Node n1, Node n2) {
        for (Edge edge : graph.getNeighbors(n1)) {
            if (edge.getTarget().equals(n2)) return true;
        }
        return false;
    }
}