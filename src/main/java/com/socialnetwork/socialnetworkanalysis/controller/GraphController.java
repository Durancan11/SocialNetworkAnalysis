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

    // Yeni Düğüm Ekleme (ÇAKIŞMA ÖNLEYİCİ VERSİYON)
    public void addNode(String name, double activity, double interaction, double connection) {
        String id = String.valueOf(graph.getAllNodes().size() + 1);
        Node node = new Node(id, name, activity, interaction, connection);

        // --- AKILLI YERLEŞİM ---
        // Rastgele yer seç ama doluysa tekrar dene
        boolean safePositionFound = false;
        int attempts = 0;

        while (!safePositionFound && attempts < 100) {
            // 50 ile 700 arası X, 50 ile 500 arası Y (Ekran ortaları)
            double tryX = Math.random() * 650 + 50;
            double tryY = Math.random() * 450 + 50;

            boolean collision = false;
            // Mevcut tüm düğümlere bak, çarpışıyor mu?
            for (Node existing : graph.getAllNodes()) {
                double dist = Math.sqrt(Math.pow(tryX - existing.getX(), 2) + Math.pow(tryY - existing.getY(), 2));
                if (dist < 60) { // 60 pikselden yakınsa çarpışma var
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                node.setX(tryX);
                node.setY(tryY);
                safePositionFound = true;
            }
            attempts++;
        }

        // Eğer 100 denemede yer bulamazsa mecbur rastgele koy
        if (!safePositionFound) {
            node.setX(Math.random() * 600 + 50);
            node.setY(Math.random() * 400 + 50);
        }
        // -----------------------

        graph.addNode(node);
        view.drawGraph(graph);
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

    public void runWelshPowell() {
        new WelshPowellAlgorithm().execute(graph, null);
        view.drawGraph(graph); // Renkler değiştiği için ekranı yenile!
        showAlert("Başarılı", "Graf renklendirildi! Komşu düğümler farklı renklere boyandı.");
    }

    // Kullanıcıya uyarı kutusu göster
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // A* Algoritması Tetikleyicisi
    // DİKKAT: Bu metod hem başlangıç hem bitiş ismini alır!
    public void runAStar(String startName, String targetName) {
        Node start = findNodeByName(startName);
        Node target = findNodeByName(targetName);

        if (start == null || target == null) {
            showAlert("Hata", "Başlangıç veya Hedef düğümü bulunamadı!");
            return;
        }

        AStarAlgorithm aStar = new AStarAlgorithm();
        aStar.findPath(graph, start, target);
        showAlert("Başarılı", "A* Algoritması tamamlandı! Sonuç konsola yazıldı.");
    }

    // Topluluk Bulma Tetikleyicisi
    public void runConnectedComponents() {
        new ConnectedComponentsAlgorithm().execute(graph, null);
        showAlert("Başarılı", "Topluluk Analizi Tamamlandı! Sonuçlar konsola yazıldı.");
    }
}