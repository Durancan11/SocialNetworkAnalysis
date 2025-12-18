package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.algorithms.*;
import com.socialnetwork.socialnetworkanalysis.model.*;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.scene.control.Alert;

public class GraphController {
    private Graph graph;
    private GraphView view;

    public GraphController(Graph graph, GraphView view) {
        this.graph = graph;
        this.view = view;
    }

    // Yeni Düğüm Ekleme
    public void addNode(String name, double activity, double interaction, double connection) {
        // ID'yi otomatik üret (Örn: "5")
        String id = String.valueOf(graph.getAllNodes().size() + 1);

        Node node = new Node(id, name, activity, interaction, connection);

        // --- KOORDİNAT GÜNCELLEMESİ YAPILDI ---
        // Eskiden +50 idi, şimdi +100 yaptık ki kenarlardan daha uzak olsunlar.
        // X ekseni (Yatay): 100 ile 600 arasında rastgele
        node.setX(Math.random() * 500 + 100);

        // Y ekseni (Dikey): 100 ile 400 arasında rastgele (Tepeden boşluk bıraktık)
        node.setY(Math.random() * 300 + 100);

        graph.addNode(node);
        view.drawGraph(graph); // Grafiği güncelle
        System.out.println("Eklendi: " + name);
    }

    // İki Düğümü Bağlama
    public void addEdge(String sourceName, String targetName) {
        Node source = findNodeByName(sourceName);
        Node target = findNodeByName(targetName);

        if (source != null && target != null) {
            graph.addEdge(source, target);
            view.drawGraph(graph); // Çizgiyi çiz
            System.out.println("Bağlandı: " + sourceName + " <-> " + targetName);
        } else {
            showAlert("Hata", "Girdiğiniz isimlerde kullanıcı bulunamadı!");
        }
    }

    // İsme göre düğüm bulucu (Yardımcı metod)
    private Node findNodeByName(String name) {
        for (Node node : graph.getAllNodes()) {
            if (node.getName().equalsIgnoreCase(name)) return node;
        }
        return null;
    }

    // --- ALGORİTMA TETİKLEYİCİLERİ ---

    public void runBFS(String startNodeName) {
        Node start = findNodeByName(startNodeName);
        if (start == null) { showAlert("Hata", "Başlangıç düğümü bulunamadı!"); return; }

        new BFSAlgorithm().execute(graph, start);
        showAlert("Başarılı", "BFS Tamamlandı! Sonuçlar konsola yazıldı.");
    }

    public void runDFS(String startNodeName) {
        Node start = findNodeByName(startNodeName);
        if (start == null) { showAlert("Hata", "Başlangıç düğümü bulunamadı!"); return; }

        new DFSAlgorithm().execute(graph, start);
        showAlert("Başarılı", "DFS Tamamlandı! Sonuçlar konsola yazıldı.");
    }

    public void runDijkstra(String startNodeName) {
        Node start = findNodeByName(startNodeName);
        if (start == null) { showAlert("Hata", "Başlangıç düğümü bulunamadı!"); return; }

        new DijkstraAlgorithm().execute(graph, start);
        showAlert("Başarılı", "Dijkstra Tamamlandı! Sonuçlar konsola yazıldı.");
    }

    public void runCentrality() {
        new DegreeCentralityAlgorithm().execute(graph, null);
        showAlert("Başarılı", "Merkezilik Analizi Tamamlandı! Sonuçlar konsola yazıldı.");
    }

    // --- GRAFİĞİ TEMİZLEME METODU ---
    public void clearGraph() {
        graph.clear(); // Modeli boşalt
        view.drawGraph(graph); // Ekranı boşalt
        System.out.println("Grafik temizlendi.");
        showAlert("Bilgi", "Ekran ve tüm veriler temizlendi. Yeni bir sayfa açtınız!");
    }

    // Kullanıcıya uyarı kutusu göster
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}