package com.socialnetwork.socialnetworkanalysis;

import com.socialnetwork.socialnetworkanalysis.controller.DataManager;
import com.socialnetwork.socialnetworkanalysis.controller.GraphController;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private Graph graph;
    private GraphView graphView;
    private GraphController controller;
    private DataManager dataManager;

    @Override
    public void start(Stage primaryStage) {
        // 1. Temel Bileşenleri Başlat
        graph = new Graph();
        graphView = new GraphView(800, 600); // Başlangıç boyutu
        controller = new GraphController(graph, graphView);
        dataManager = new DataManager();

        // 2. SOL PANEL (Kontrol Paneli) Oluştur
        VBox sideBar = new VBox(10); // Elemanlar arası 10px boşluk
        sideBar.setPadding(new Insets(15));
        sideBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        sideBar.setPrefWidth(250);

        // --- BÖLÜM 1: YENİ KİŞİ EKLE ---
        Label lblAdd = new Label("YENİ KİŞİ EKLE");
        lblAdd.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtName = new TextField(); txtName.setPromptText("İsim (Örn: Ali)");
        TextField txtAct = new TextField("0.5"); txtAct.setPromptText("Aktiflik (0.0 - 1.0)");
        TextField txtInt = new TextField("0.5"); txtInt.setPromptText("Etkileşim (0.0 - 1.0)");
        TextField txtConn = new TextField("5"); txtConn.setPromptText("Bağlantı Sayısı");

        Button btnAddNode = new Button("Kişiyi Ekle");
        btnAddNode.setMaxWidth(Double.MAX_VALUE);
        btnAddNode.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnAddNode.setOnAction(e -> {
            try {
                String name = txtName.getText();
                double act = Double.parseDouble(txtAct.getText());
                double inter = Double.parseDouble(txtInt.getText());
                double conn = Double.parseDouble(txtConn.getText());

                controller.addNode(name, act, inter, conn);
                txtName.clear();
            } catch (Exception ex) {
                showAlert("Hata", "Lütfen sayısal değerleri doğru giriniz!");
            }
        });

        // --- BÖLÜM 2: BAĞLANTI KUR ---
        Label lblEdge = new Label("BAĞLANTI KUR");
        lblEdge.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtSource = new TextField(); txtSource.setPromptText("Kimden (Örn: Ali)");
        TextField txtTarget = new TextField(); txtTarget.setPromptText("Kime (Örn: Ayşe)");

        Button btnAddEdge = new Button("Bağlantı Ekle");
        btnAddEdge.setMaxWidth(Double.MAX_VALUE);
        btnAddEdge.setOnAction(e -> {
            controller.addEdge(txtSource.getText(), txtTarget.getText());
        });

        // --- BÖLÜM 3: ALGORİTMALAR ---
        Label lblAlgo = new Label("ANALİZ & ALGORİTMA");
        lblAlgo.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField txtStartNode = new TextField(); txtStartNode.setPromptText("Başlangıç Kişisi (İsim)");

        Button btnBFS = new Button("BFS (Genişlik)");
        btnBFS.setMaxWidth(Double.MAX_VALUE);
        btnBFS.setOnAction(e -> controller.runBFS(txtStartNode.getText()));

        Button btnDFS = new Button("DFS (Derinlik)");
        btnDFS.setMaxWidth(Double.MAX_VALUE);
        btnDFS.setOnAction(e -> controller.runDFS(txtStartNode.getText()));

        Button btnDijkstra = new Button("Dijkstra (En Kısa Yol)");
        btnDijkstra.setMaxWidth(Double.MAX_VALUE);
        btnDijkstra.setOnAction(e -> controller.runDijkstra(txtStartNode.getText()));

        Button btnCentrality = new Button("En Popüler Kim? (Merkezilik)");
        btnCentrality.setMaxWidth(Double.MAX_VALUE);
        btnCentrality.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btnCentrality.setOnAction(e -> controller.runCentrality());

        // --- BÖLÜM 4: DOSYA VE TEMİZLİK ---
        Separator sep = new Separator();

        Button btnSave = new Button("Kaydet (CSV)");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setOnAction(e -> dataManager.saveGraph(graph, "."));

        Button btnLoad = new Button("Yükle (CSV)");
        btnLoad.setMaxWidth(Double.MAX_VALUE);
        btnLoad.setOnAction(e -> {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
            System.out.println("Veriler yüklendi ve çizildi.");
        });

        // --- YENİ EKLENEN: TEMİZLE BUTONU ---
        Button btnClear = new Button("TEMİZLE (Sıfırla)");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;"); // Kırmızı
        // DİKKAT: Bu metod GraphController içinde olmalı!
        btnClear.setOnAction(e -> controller.clearGraph());

        // Hepsini Panele Ekle
        sideBar.getChildren().addAll(
                lblAdd, txtName, txtAct, txtInt, txtConn, btnAddNode,
                new Separator(),
                lblEdge, txtSource, txtTarget, btnAddEdge,
                new Separator(),
                lblAlgo, txtStartNode, btnBFS, btnDFS, btnDijkstra, btnCentrality,
                sep, btnSave, btnLoad, btnClear // btnClear buraya eklendi
        );

        // 3. ANA DÜZEN (BorderPane)
        BorderPane root = new BorderPane();
        root.setLeft(sideBar);
        root.setCenter(graphView);

        // 4. SAHNEYİ BAŞLAT
        Scene scene = new Scene(root, 1100, 700);

        // --- YENİ EKLENEN: RESPONSIVE (ESNEK) EKRAN ---
        // Pencere büyüyünce grafik alanı da büyüsün
        graphView.widthProperty().bind(root.widthProperty().subtract(250)); // Panel payını düş
        graphView.heightProperty().bind(root.heightProperty());

        // Boyut değişince yeniden çizim yap (GraphView içindeki redraw metodu)
        graphView.widthProperty().addListener(obs -> graphView.redraw());
        graphView.heightProperty().addListener(obs -> graphView.redraw());

        primaryStage.setTitle("Sosyal Ağ Analizi - Komuta Merkezi");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Otomatik Yükleme Denemesi
        try {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
        } catch (Exception e) {
            System.out.println("Başlangıç verisi bulunamadı, temiz açılıyor.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}