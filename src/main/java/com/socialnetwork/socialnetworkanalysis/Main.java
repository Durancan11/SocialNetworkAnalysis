package com.socialnetwork.socialnetworkanalysis;

import com.socialnetwork.socialnetworkanalysis.controller.DataManager;
import com.socialnetwork.socialnetworkanalysis.controller.GraphController;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Objects;

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
        sidebarContent.setPadding(new Insets(25));

        // LOGO / BAŞLIK
        Label title = new Label("NEO-GRAPH");
        title.getStyleClass().add("header-label");

        // --- BÖLÜM 1: GİRİŞ ---
        Label lblSec1 = new Label("VERİ GİRİŞİ");
        lblSec1.getStyleClass().add("section-label");

        TextField txtName = new TextField(); txtName.setPromptText("Kullanıcı Adı");

        HBox statsBox = new HBox(5);
        TextField txtAct = new TextField("0.5"); txtAct.setPromptText("Akt.");
        TextField txtInt = new TextField("0.5"); txtInt.setPromptText("Etk.");
        TextField txtConn = new TextField("5"); txtConn.setPromptText("Puan");
        statsBox.getChildren().addAll(txtAct, txtInt, txtConn);
        HBox.setHgrow(txtAct, Priority.ALWAYS); HBox.setHgrow(txtInt, Priority.ALWAYS); HBox.setHgrow(txtConn, Priority.ALWAYS);

        Button btnAdd = new Button("+ SİSTEME EKLE");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> {
            try {
                controller.addNode(txtName.getText(), Double.parseDouble(txtAct.getText()), Double.parseDouble(txtInt.getText()), Double.parseDouble(txtConn.getText()));
                txtName.clear();
            } catch (Exception ex) { }
        });

        // --- BÖLÜM 2: BAĞLANTI HATTI ---
        Label lblSec2 = new Label("BAĞLANTI HATTI");
        lblSec2.getStyleClass().add("section-label");

        TextField txtSrc = new TextField(); txtSrc.setPromptText("Kaynak Node");
        TextField txtDst = new TextField(); txtDst.setPromptText("Hedef Node");

        Button btnLink = new Button("BAĞLANTI KUR");
        btnLink.setId("btnSecondary");
        btnLink.setMaxWidth(Double.MAX_VALUE);
        btnLink.setOnAction(e -> controller.addEdge(txtSrc.getText(), txtDst.getText()));

        Button btnUnlink = new Button("BAĞI KOPAR (SİL)");
        btnUnlink.setId("btnDanger");
        btnUnlink.setMaxWidth(Double.MAX_VALUE);
        btnUnlink.setOnAction(e -> controller.removeEdge(txtSrc.getText(), txtDst.getText()));

        // --- BÖLÜM 3: ANALİZ MOTORU ---
        Label lblSec3 = new Label("ANALİZ MOTORU");
        lblSec3.getStyleClass().add("section-label");

        TextField txtStart = new TextField(); txtStart.setPromptText("Başlangıç Noktası");

        GridPane gridAlgo = new GridPane();
        gridAlgo.setHgap(5); gridAlgo.setVgap(5);

        Button btnBFS = new Button("BFS"); btnBFS.setMaxWidth(Double.MAX_VALUE); btnBFS.setId("btnSecondary");
        Button btnDFS = new Button("DFS"); btnDFS.setMaxWidth(Double.MAX_VALUE); btnBFS.setId("btnSecondary"); // ID düzeltildi
        Button btnDijk = new Button("Dijkstra"); btnDijk.setMaxWidth(Double.MAX_VALUE); btnDijk.setId("btnSecondary"); // Değişken adı düzeltildi
        Button btnAStar = new Button("A* Path"); btnAStar.setMaxWidth(Double.MAX_VALUE); btnAStar.setId("btnSecondary"); // Değişken adı düzeltildi

        gridAlgo.add(btnBFS, 0, 0); gridAlgo.add(btnDFS, 1, 0);
        gridAlgo.add(btnDijk, 0, 1); gridAlgo.add(btnAStar, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridAlgo.getColumnConstraints().addAll(col1, col2);

        btnBFS.setOnAction(e -> controller.runBFS(txtStart.getText()));
        btnDFS.setOnAction(e -> controller.runDFS(txtStart.getText()));
        btnDijk.setOnAction(e -> controller.runDijkstra(txtStart.getText()));
        btnAStar.setOnAction(e -> controller.runAStar(txtSrc.getText(), txtDst.getText()));

        Button btnCentral = new Button("MERKEZİLİK ANALİZİ");
        btnCentral.setMaxWidth(Double.MAX_VALUE); btnCentral.setId("btnSecondary");
        btnCentral.setOnAction(e -> controller.runCentrality());

        Button btnColor = new Button("RENKLENDİRME MODU");
        btnColor.setMaxWidth(Double.MAX_VALUE);
        btnColor.setOnAction(e -> controller.runWelshPowell());

        Button btnComm = new Button("TOPLULUK TARAMASI");
        btnComm.setMaxWidth(Double.MAX_VALUE); btnComm.setId("btnSecondary");
        btnComm.setOnAction(e -> controller.runConnectedComponents());

        // --- BÖLÜM 4: SİSTEM & TEST (GÜNCELLENEN KISIM BURASIYDI) ---
        Label lblSec4 = new Label("SİSTEM & TEST");
        lblSec4.getStyleClass().add("section-label");

        HBox fileBox = new HBox(5);
        Button btnSave = new Button("KAYDET"); btnSave.setMaxWidth(Double.MAX_VALUE); btnSave.setId("btnSecondary");
        Button btnLoad = new Button("YÜKLE"); btnLoad.setMaxWidth(Double.MAX_VALUE); btnLoad.setId("btnSecondary");
        HBox.setHgrow(btnSave, Priority.ALWAYS); HBox.setHgrow(btnLoad, Priority.ALWAYS);
        fileBox.getChildren().addAll(btnSave, btnLoad);

        btnSave.setOnAction(e -> dataManager.saveGraph(graph, "."));
        btnLoad.setOnAction(e -> {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
        });

        // --- YENİ EKLENEN TEST BUTONU ---
        Button btnRandom = new Button("🧪 TEST: RASTGELE 50 KİŞİ");
        btnRandom.setMaxWidth(Double.MAX_VALUE);
        btnRandom.setId("btnSecondary");
        btnRandom.setOnAction(e -> controller.generateRandomGraph(50));
        // -------------------------------

        Button btnClear = new Button("SİSTEMİ İMHA ET (RESET)");
        btnClear.setId("btnDanger");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> controller.clearGraph());

        sidebarContent.getChildren().addAll(
                title,
                lblSec1, txtName, statsBox, btnAdd,
                lblSec2, txtSrc, txtDst, btnLink, btnUnlink,
                lblSec3, txtStart, gridAlgo, btnCentral, btnColor, btnComm,
                lblSec4, fileBox, btnRandom, btnClear
        );

        // --- SAHNE VE PENCERE AYARLARI (SİLİNEN KISIM BURASIYDI) ---
        ScrollPane scrollPane = new ScrollPane(sidebarContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("sidebar");
        scrollPane.setPrefWidth(280);

        BorderPane root = new BorderPane();
        root.setLeft(scrollPane);
        root.setCenter(graphView);

        Scene scene = new Scene(root, 1100, 750);
        try { scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()); } catch (Exception e) {}

        graphView.widthProperty().bind(root.widthProperty().subtract(280));
        graphView.heightProperty().bind(root.heightProperty());
        graphView.widthProperty().addListener(o -> graphView.redraw());
        graphView.heightProperty().addListener(o -> graphView.redraw());

        primaryStage.setTitle("Neo-Graph Intelligence System");
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            graph = dataManager.loadGraph(".");
            controller = new GraphController(graph, graphView);
            graphView.drawGraph(graph);
        } catch (Exception e) {}
    } // START METODU BURADA BİTİYOR

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}