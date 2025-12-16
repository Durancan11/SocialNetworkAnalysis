package com.socialnetwork.socialnetworkanalysis;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. Graf Verisi Oluştur (Örnek Veriler)
        Graph graph = new Graph();

        // Koordinatları (x, y) elle veriyoruz ki ekranda düzgün dursunlar
        Node n1 = new Node("1", "Ahmet", 10, 5, 2);
        n1.setX(100); n1.setY(100); // Sol üstte

        Node n2 = new Node("2", "Mehmet", 5, 2, 1);
        n2.setX(300); n2.setY(100); // Sağ üstte

        Node n3 = new Node("3", "Ayşe", 20, 10, 5);
        n3.setX(200); n3.setY(250); // Aşağıda ortada

        Node n4 = new Node("4", "Fatma", 15, 8, 4);
        n4.setX(400); n4.setY(300); // Sağ altta

        graph.addEdge(n1, n2); // Ahmet - Mehmet
        graph.addEdge(n2, n3); // Mehmet - Ayşe
        graph.addEdge(n1, n3); // Ahmet - Ayşe
        graph.addEdge(n3, n4); // Ayşe - Fatma

        // 2. Görünümü (Canvas) Hazırla
        GraphView graphView = new GraphView(800, 600);
        graphView.drawGraph(graph); // Grafı çizdir!

        // 3. Pencere Düzeni
        BorderPane root = new BorderPane();
        root.setCenter(graphView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Sosyal Ağ Analizi - Proje Görsel Testi");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}