package com.socialnetwork.socialnetworkanalysis;

import com.socialnetwork.socialnetworkanalysis.controller.DataManager;
import com.socialnetwork.socialnetworkanalysis.controller.GraphController;
import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.Objects;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private Graph graph;
    private GraphView graphView;
    private GraphController controller;
    private DataManager dataManager;

    @Override
    public void start(Stage primaryStage) {
        // 1. Başlangıç
        graph = new Graph();
        graphView = new GraphView(800, 600);
        controller = new GraphController(graph, graphView);
        dataManager = new DataManager();

        // --- SOL PANEL (SIDEBAR) ---
        VBox sidebarContent = new VBox(15);
        sidebarContent.setPadding(new Insets(15));

        // LOGO / BAŞLIK
        Label title = new Label("Graph Analysis");
        title.getStyleClass().add("header-label");

        // --- BÖLÜM 1: GİRİŞ ---
        Label lblSec1 = new Label("Kullanıcı Verileri");
        lblSec1.getStyleClass().add("section-label");

        TextField txtName = new TextField(); txtName.setPromptText("Kullanıcı Adı");

        HBox statsBox = new HBox(5);
        TextField txtAct = new TextField("0.5"); txtAct.setPromptText("Aktiflik");
        TextField txtInt = new TextField("0.5"); txtInt.setPromptText("Etkileşim");
        TextField txtConn = new TextField("5"); txtConn.setPromptText("Bağlantı");
        statsBox.getChildren().addAll(txtAct, txtInt, txtConn);
        HBox.setHgrow(txtAct, Priority.ALWAYS); HBox.setHgrow(txtInt, Priority.ALWAYS); HBox.setHgrow(txtConn, Priority.ALWAYS);

        Button btnAdd = new Button("+ Sisteme Ekle");
        btnAdd.setId("btnAdd");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> {
            try {
                controller.addNode(txtName.getText(), Double.parseDouble(txtAct.getText()), Double.parseDouble(txtInt.getText()), Double.parseDouble(txtConn.getText()));
                txtName.clear();
            } catch (Exception ex) { }
        });

        // --- BÖLÜM 2: BAĞLANTI HATTI ---
        Label lblSec2 = new Label("Bağlantı Yönetimi");
        lblSec2.getStyleClass().add("section-label");

        TextField txtSrc = new TextField(); txtSrc.setPromptText("Kaynak Kullanıcı");
        TextField txtDst = new TextField(); txtDst.setPromptText("Hedef Kullanıcı");

        // --- AKILLI BAĞLANTI BUTONU ---
        Button btnLink = new Button("Bağlantı Kur");
        btnLink.setId("btnSecondary");
        btnLink.setMaxWidth(Double.MAX_VALUE);

        btnLink.setOnAction(e -> {
            String srcName = txtSrc.getText().trim();
            String dstName = txtDst.getText().trim();

            if(srcName.isEmpty() || dstName.isEmpty()) {
                showAlert("Uyarı", "Lütfen her iki kullanıcı adını da giriniz.");
                return;
            }

            // 1. Kullanıcılar var mı diye kontrol et
            Node srcNode = null;
            Node dstNode = null;
            for(Node n : graph.getAllNodes()) {
                if(n.getName().equalsIgnoreCase(srcName)) srcNode = n;
                if(n.getName().equalsIgnoreCase(dstName)) dstNode = n;
            }

            if(srcNode == null || dstNode == null) {
                showAlert("Hata", "Kullanıcılardan biri veya ikisi bulunamadı!");
                return;
            }

            // 2. Bağlantı zaten var mı diye kontrol et
            boolean exists = false;
            for(Edge edge : graph.getNeighbors(srcNode)) {
                if(edge.getTarget() == dstNode) {
                    exists = true;
                    break;
                }
            }

            if(exists) {
                // VARSA UYARI VER
                showAlert("Bilgi", srcName + " ve " + dstName + " arasında zaten bağlantı var!");
            } else {
                // YOKSA EKLE
                controller.addEdge(srcName, dstName);
            }
        });

        Button btnUnlink = new Button("Bağlantı Sil");
        btnUnlink.setId("btnDanger");
        btnUnlink.setMaxWidth(Double.MAX_VALUE);
        btnUnlink.setOnAction(e -> controller.removeEdge(txtSrc.getText(), txtDst.getText()));

        // --- BÖLÜM 3: ANALİZ MOTORU ---
        Label lblSec3 = new Label("Analiz Algoritmaları");
        lblSec3.getStyleClass().add("section-label");

        TextField txtStart = new TextField(); txtStart.setPromptText("Başlangıç Düğümü");

        GridPane gridAlgo = new GridPane();
        gridAlgo.setHgap(5); gridAlgo.setVgap(5);

        Button btnBFS = new Button("BFS Analizi"); btnBFS.setMaxWidth(Double.MAX_VALUE); btnBFS.setId("btnSecondary");
        Button btnDFS = new Button("DFS Analizi"); btnDFS.setMaxWidth(Double.MAX_VALUE); btnDFS.setId("btnSecondary");
        Button btnDijk = new Button("Dijkstra (Yol)"); btnDijk.setMaxWidth(Double.MAX_VALUE); btnDijk.setId("btnSecondary");
        Button btnAStar = new Button("A* (Sezgisel)"); btnAStar.setMaxWidth(Double.MAX_VALUE); btnAStar.setId("btnSecondary");

        gridAlgo.add(btnBFS, 0, 0); gridAlgo.add(btnDFS, 1, 0);
        gridAlgo.add(btnDijk, 0, 1); gridAlgo.add(btnAStar, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridAlgo.getColumnConstraints().addAll(col1, col2);

        btnBFS.setOnAction(e -> controller.runBFS(txtStart.getText()));
        btnDFS.setOnAction(e -> controller.runDFS(txtStart.getText()));
        btnDijk.setOnAction(e -> controller.runDijkstra(txtStart.getText()));
        btnAStar.setOnAction(e -> controller.runAStar(txtSrc.getText(), txtDst.getText()));

        Button btnCentral = new Button("Merkezilik Analizi");
        btnCentral.setMaxWidth(Double.MAX_VALUE); btnCentral.setId("btnSecondary");
        btnCentral.setOnAction(e -> controller.runCentrality());

        Button btnColor = new Button("Graf Renklendirme");
        btnColor.setMaxWidth(Double.MAX_VALUE);
        btnColor.setOnAction(e -> controller.runWelshPowell());

        Button btnComm = new Button("Topluluk Tespiti");
        btnComm.setMaxWidth(Double.MAX_VALUE); btnComm.setId("btnSecondary");
        btnComm.setOnAction(e -> controller.runConnectedComponents());

        // --- BÖLÜM 4: SİSTEM & VERİ ---
        Label lblSec4 = new Label("Sistem & Veri");
        lblSec4.getStyleClass().add("section-label");

        HBox fileBox = new HBox(5);
        Button btnSave = new Button("Kaydet");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setId("btnSecondary");

        Button btnLoad = new Button("Yükle");
        btnLoad.setMaxWidth(Double.MAX_VALUE);
        btnLoad.setId("btnSecondary");

        // --- GÖRÜNTÜYÜ İNDİR BUTONU ---
        Button btnSnapshot = new Button("Görüntüyü Farklı Kaydet");
        btnSnapshot.setId("btnSecondary");
        btnSnapshot.setMaxWidth(Double.MAX_VALUE);

        btnSnapshot.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Grafiği Resim Olarak Kaydet");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Dosyası", "*.png"));
            fileChooser.setInitialFileName("grafik_analiz_" + System.currentTimeMillis() + ".png");

            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill(Color.WHITE);
                    params.setTransform(Transform.scale(2, 2));

                    WritableImage image = graphView.snapshot(params, null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Başarılı");
                    alert.setHeaderText("Kaydedildi!");
                    alert.setContentText("Resim şuraya kaydedildi:\n" + file.getAbsolutePath());
                    alert.showAndWait();

                } catch (IOException ex) {
                    showAlert("Hata", "Kaydedilemedi: " + ex.getMessage());
                }
            }
        });

        HBox.setHgrow(btnSave, Priority.ALWAYS); HBox.setHgrow(btnLoad, Priority.ALWAYS);
        fileBox.getChildren().addAll(btnSave, btnLoad);

        // --- KAYDET BUTONU (CSV) ---
        btnSave.setOnAction(e -> {
            System.out.println("💾 Kaydet butonuna basıldı...");
            try {
                if (dataManager == null) {
                    showAlert("Kritik Hata", "DataManager bulunamadı!"); return;
                }
                dataManager.saveGraph(graph, ".");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("İşlem Başarılı");
                alert.setHeaderText("Kayıt Tamamlandı");
                alert.setContentText("Veriler 'nodes.csv' dosyasına proje klasörüne kaydedildi.");
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Kayıt Hatası", "Hata Detayı: " + ex.getMessage());
            }
        });

        btnLoad.setOnAction(e -> {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
        });

        // --- TEST BUTONLARI ---
        HBox testBox = new HBox(5);
        Button btnRandom10 = new Button("Test: 10 Düğüm");
        Button btnRandom50 = new Button("Test: 50 Düğüm");
        btnRandom10.setId("btnSecondary");
        btnRandom50.setId("btnSecondary");
        btnRandom10.setMaxWidth(Double.MAX_VALUE);
        btnRandom50.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRandom10, Priority.ALWAYS);
        HBox.setHgrow(btnRandom50, Priority.ALWAYS);

        btnRandom10.setOnAction(e -> controller.generateRandomGraph(10));
        btnRandom50.setOnAction(e -> controller.generateRandomGraph(50));
        testBox.getChildren().addAll(btnRandom10, btnRandom50);

        // --- DÜZENLEME VE SİLME ---
        Button btnLayout = new Button("Otomatik Düzenle");
        btnLayout.setId("btnSecondary");
        btnLayout.setMaxWidth(Double.MAX_VALUE);
        btnLayout.setOnAction(e -> controller.runSpringLayout());

        Button btnClear = new Button("Sistemi Temizle");
        btnClear.setId("btnDanger");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> controller.clearGraph());

        sidebarContent.getChildren().addAll(
                title,
                lblSec1, txtName, statsBox, btnAdd,
                lblSec2, txtSrc, txtDst, btnLink, btnUnlink,
                lblSec3, txtStart, gridAlgo, btnCentral, btnColor, btnComm,
                lblSec4, fileBox, btnSnapshot, testBox, btnLayout, btnClear
        );

        // --- SAHNE AYARLARI ---
        ScrollPane scrollPane = new ScrollPane(sidebarContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("sidebar");
        scrollPane.setPrefWidth(280);

        BorderPane root = new BorderPane();
        root.setLeft(scrollPane);
        root.setCenter(graphView);

        Scene scene = new Scene(root, 1100, 750);

        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS Dosyası Bulunamadı! '/style.css' yolunu kontrol et.");
        }

        graphView.widthProperty().bind(root.widthProperty().subtract(280));
        graphView.heightProperty().bind(root.heightProperty());
        graphView.widthProperty().addListener(o -> graphView.redraw());
        graphView.heightProperty().addListener(o -> graphView.redraw());

        primaryStage.setTitle("Graph Intelligence System - v2.0");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
        } catch (Exception e) {}
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}