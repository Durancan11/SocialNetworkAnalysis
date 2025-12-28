package com.socialnetwork.socialnetworkanalysis.view;

import com.socialnetwork.socialnetworkanalysis.controller.GraphController;
import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class GraphView extends Canvas {
    private Graph currentGraph;
    private GraphController controller;
    private Node draggedNode = null;

    public GraphView(double width, double height) {
        super(width, height);

        // --- ETKİLEŞİMLER ---
        this.setOnMousePressed(e -> {
            if(currentGraph==null) return;
            for(Node n : currentGraph.getAllNodes()) {
                if(Math.sqrt(Math.pow(e.getX()-n.getX(),2)+Math.pow(e.getY()-n.getY(),2)) <= 30) {
                    draggedNode = n; break;
                }
            }
        });
        this.setOnMouseDragged(e -> {
            if(draggedNode != null) {
                draggedNode.setX(Math.max(30, Math.min(getWidth()-30, e.getX())));
                draggedNode.setY(Math.max(30, Math.min(getHeight()-30, e.getY())));
                redraw();
            }
        });
        this.setOnMouseReleased(e -> draggedNode = null);
        this.setOnMouseClicked(e -> {
            if(currentGraph==null) return;
            for(Node n : currentGraph.getAllNodes()) {
                if(Math.sqrt(Math.pow(e.getX()-n.getX(),2)+Math.pow(e.getY()-n.getY(),2)) <= 30) {
                    showNodeActions(n); break;
                }
            }
        });
    }

    public void setController(GraphController c) { this.controller = c; }

    // --- EKSİK OLAN METOD BURASIYDI! ---
    public void redraw() {
        if (currentGraph != null) {
            drawGraph(currentGraph);
        }
    }
    // -----------------------------------

    // --- POP-UP MENÜ ---
    private void showNodeActions(Node node) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Düğüm Detayı");
        alert.setHeaderText("Seçilen Kullanıcı: " + node.getName());
        String info = String.format("📊 İSTATİSTİKLER\n───────────────────\n🔹 Aktiflik Değeri: %.2f\n🔹 Etkileşim Skoru: %.2f\n🔹 Bağlantı Puanı: %.2f",
                node.getActivity(), node.getInteraction(), node.getConnectionCount());
        alert.setContentText(info);

        ButtonType btnUpdate = new ButtonType("Güncelle");
        ButtonType btnDelete = new ButtonType("Sil", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnClose = new ButtonType("Kapat", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnUpdate, btnDelete, btnClose);

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent()) {
            if(res.get()==btnDelete && controller!=null) { currentGraph.removeNode(node); redraw(); }
            else if(res.get()==btnUpdate) showUpdateDialog(node);
        }
    }

    private void showUpdateDialog(Node node) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Veri Güncelleme");
        dialog.setHeaderText("Düzenle: " + node.getName());
        ButtonType ok = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 50, 10, 10));
        TextField t1 = new TextField(String.valueOf(node.getActivity()));
        TextField t2 = new TextField(String.valueOf(node.getInteraction()));
        TextField t3 = new TextField(String.valueOf(node.getConnectionCount()));

        grid.add(new Label("Aktiflik:"),0,0); grid.add(t1,1,0);
        grid.add(new Label("Etkileşim:"),0,1); grid.add(t2,1,1);
        grid.add(new Label("Puan:"),0,2); grid.add(t3,1,2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if(b==ok) {
                try {
                    if(controller!=null) controller.updateNode(node, Double.parseDouble(t1.getText()), Double.parseDouble(t2.getText()), Double.parseDouble(t3.getText()));
                    return true;
                } catch(Exception e) { return false; }
            } return null;
        });
        dialog.showAndWait();
    }

    // --- ÇİZİM MOTORU (AKADEMİK STİL) ---
    @Override public boolean isResizable() { return true; }
    @Override public double prefWidth(double h) { return getWidth(); }
    @Override public double prefHeight(double w) { return getHeight(); }

    public void drawGraph(Graph graph) {
        this.currentGraph = graph;
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth(), h = getHeight();

        // 1. ARKA PLAN (AKADEMİK GRAFİK KAĞIDI)
        gc.setFill(Color.web("#ffffff")); // Bembeyaz Zemin
        gc.fillRect(0, 0, w, h);

        // Milimetrik Izgara
        gc.setStroke(Color.web("#e0e0e0")); // Çok açık gri
        gc.setLineWidth(1);
        for(int i=0; i<w; i+=20) gc.strokeLine(i, 0, i, h);
        for(int i=0; i<h; i+=20) gc.strokeLine(0, i, w, i);

        // Büyük kareler
        gc.setStroke(Color.web("#cbd5e0"));
        for(int i=0; i<w; i+=100) gc.strokeLine(i, 0, i, h);
        for(int i=0; i<h; i+=100) gc.strokeLine(0, i, w, i);

        if (graph == null) return;

        // 2. KENARLAR
        gc.setLineWidth(1.5);
        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getNeighbors(node)) {
                Node t = edge.getTarget();
                gc.setStroke(Color.web("#7f8c8d"));
                gc.strokeLine(node.getX(), node.getY(), t.getX(), t.getY());

                double mx = (node.getX()+t.getX())/2;
                double my = (node.getY()+t.getY())/2;

                gc.setFill(Color.WHITE);
                gc.setStroke(Color.web("#95a5a6"));
                gc.strokeRoundRect(mx-12, my-8, 24, 16, 4, 4);
                gc.fillRoundRect(mx-12, my-8, 24, 16, 4, 4);

                gc.setFill(Color.web("#2c3e50"));
                gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.format("%.0f", edge.getWeight()), mx, my+4);
            }
        }

        // 3. DÜĞÜMLER
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0,0,0,0.2));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        gc.setEffect(shadow);

        for (Node node : graph.getAllNodes()) {
            drawAcademicNode(gc, node);
        }

        gc.setEffect(null);
    }

    private void drawAcademicNode(GraphicsContext gc, Node node) {
        double r = 20;
        double x = node.getX();
        double y = node.getY();

        Color fillColor = node.getColor();
        if(fillColor.equals(Color.web("#00e5ff"))) {
            fillColor = Color.web("#3498db");
        }

        gc.setFill(fillColor);
        gc.fillOval(x-r, y-r, r*2, r*2);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(x-r, y-r, r*2, r*2);

        gc.setFill(Color.web("#2c3e50"));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(node.getName(), x, y + r + 15);
    }
}