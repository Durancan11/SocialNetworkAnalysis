package com.socialnetwork.socialnetworkanalysis.view;

import com.socialnetwork.socialnetworkanalysis.controller.GraphController;
import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class GraphView extends Canvas {
    private Graph currentGraph;
    private GraphController controller; // Controller bağlantısı

    public GraphView(double width, double height) {
        super(width, height);
        this.setOnMouseClicked(event -> {
            if (currentGraph == null) return;
            for (Node node : currentGraph.getAllNodes()) {
                double dist = Math.sqrt(Math.pow(event.getX()-node.getX(), 2) + Math.pow(event.getY()-node.getY(), 2));
                if (dist <= 25) { showNodeActions(node); break; }
            }
        });
    }

    // Controller'ı kaydet (Main çağırdıktan sonra GraphController burayı set edecek)
    public void setController(GraphController controller) {
        this.controller = controller;
    }

    private void showNodeActions(Node node) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Düğüm Protokolü");
        alert.setHeaderText("HEDEF: " + node.getName());
        String info = String.format("Aktiflik: %.2f\nEtkileşim: %.2f\nPuan: %.2f",
                node.getActivity(), node.getInteraction(), node.getConnectionCount());
        alert.setContentText(info);

        // Butonlar
        ButtonType btnUpdate = new ButtonType("BİLGİLERİ GÜNCELLE");
        ButtonType btnDelete = new ButtonType("HEDEFİ SİL", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnClose = new ButtonType("İPTAL", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnUpdate, btnDelete, btnClose);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnDelete) {
                // Silme
                currentGraph.removeNode(node);
                redraw();
            } else if (result.get() == btnUpdate) {
                // Güncelleme Ekranını Aç
                showUpdateDialog(node);
            }
        }
    }

    // --- YENİ: GÜNCELLEME PENCERESİ ---
    private void showUpdateDialog(Node node) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Veri Güncelleme");
        dialog.setHeaderText(node.getName() + " için yeni değerleri giriniz:");

        ButtonType loginButtonType = new ButtonType("Kaydet", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField act = new TextField(String.valueOf(node.getActivity()));
        TextField intel = new TextField(String.valueOf(node.getInteraction()));
        TextField conn = new TextField(String.valueOf(node.getConnectionCount()));

        grid.add(new Label("Aktiflik:"), 0, 0); grid.add(act, 1, 0);
        grid.add(new Label("Etkileşim:"), 0, 1); grid.add(intel, 1, 1);
        grid.add(new Label("Puan:"), 0, 2); grid.add(conn, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                try {
                    double a = Double.parseDouble(act.getText());
                    double i = Double.parseDouble(intel.getText());
                    double c = Double.parseDouble(conn.getText());
                    // Controller üzerinden güncelle (Ağırlıklar hesaplansın diye)
                    if (controller != null) controller.updateNode(node, a, i, c);
                    return true;
                } catch (Exception e) { return false; }
            }
            return null;
        });
        dialog.showAndWait();
    }

    // --- ÇİZİM İŞLEMLERİ (AYNEN KORUNDU) ---
    @Override public boolean isResizable() { return true; }
    @Override public double prefWidth(double height) { return getWidth(); }
    @Override public double prefHeight(double width) { return getHeight(); }

    public void drawGraph(Graph graph) {
        this.currentGraph = graph;
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.web("#121212")); gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setStroke(Color.web("#222222")); gc.setLineWidth(1);
        for(int i=0;i<getWidth();i+=40) gc.strokeLine(i,0,i,getHeight());
        for(int i=0;i<getHeight();i+=40) gc.strokeLine(0,i,getWidth(),i);

        if (graph == null) return;

        gc.setLineWidth(1.5);
        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getNeighbors(node)) {
                Node t = edge.getTarget();
                gc.setStroke(Color.rgb(255, 255, 255, 0.3));
                gc.strokeLine(node.getX(), node.getY(), t.getX(), t.getY());

                double mx=(node.getX()+t.getX())/2, my=(node.getY()+t.getY())/2;
                gc.setFill(Color.web("#1e1e1e")); gc.fillRoundRect(mx-15,my-8,30,16,5,5);
                gc.setStroke(Color.web("#00e5ff")); gc.strokeRoundRect(mx-15,my-8,30,16,5,5);
                gc.setFill(Color.web("#00e5ff")); gc.setFont(Font.font("Consolas",10));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.format("%.1f", edge.getWeight()), mx, my+4);
            }
        }
        DropShadow glow = new DropShadow(); glow.setColor(Color.web("#00e5ff")); glow.setRadius(15); gc.setEffect(glow);
        for (Node node : graph.getAllNodes()) drawNeonNode(gc, node);
        gc.setEffect(null);
    }

    public void redraw() { if(currentGraph!=null) drawGraph(currentGraph); }

    private void drawNeonNode(GraphicsContext gc, Node node) {
        double r = 22; Color nc = node.getColor().deriveColor(0, 1.2, 1.2, 1);
        gc.setFill(nc); gc.fillOval(node.getX()-r, node.getY()-r, r*2, r*2);
        gc.setFill(Color.rgb(255,255,255,0.7)); gc.fillOval(node.getX()-r/3, node.getY()-r/3, r/1.5, r/1.5);
        gc.setFill(Color.WHITE); gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER); gc.fillText(node.getName(), node.getX(), node.getY()+r+15);
    }
}