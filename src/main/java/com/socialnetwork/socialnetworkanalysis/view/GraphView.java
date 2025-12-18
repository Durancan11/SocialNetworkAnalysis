package com.socialnetwork.socialnetworkanalysis.view;

import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GraphView extends Canvas {
    // Grafiği hafızada tutalım ki ekran boyutu değişince tekrar çizebilelim
    private Graph currentGraph;

    public GraphView(double width, double height) {
        super(width, height);

        // TIKLAMA OLAYINI DİNLE
        this.setOnMouseClicked(event -> {
            if (currentGraph == null) return;

            double mouseX = event.getX();
            double mouseY = event.getY();

            // Tüm düğümleri kontrol et: Acaba hangisine tıkladık?
            for (Node node : currentGraph.getAllNodes()) {
                // Pisagor bağıntısı ile mesafe ölç (Merkeze yakın mı?)
                double distance = Math.sqrt(Math.pow(mouseX - node.getX(), 2) + Math.pow(mouseY - node.getY(), 2));

                // Düğümün yarıçapı 20 idi, eğer mesafe 20'den küçükse tıklanmıştır
                if (distance <= 20) {
                    showNodeInfo(node);
                    break; // Bulduk, döngüden çık
                }
            }
        });
    }

    // Bilgi Penceresi Açan Metod
    private void showNodeInfo(Node node) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Düğüm Bilgisi");
        alert.setHeaderText(node.getName() + " (" + node.getId() + ")");

        String info = "Aktiflik: " + node.getActivity() + "\n" +
                "Etkileşim: " + node.getInteraction() + "\n" +
                "Bağlantı Puanı: " + node.getConnectionCount() + "\n" +
                "Koordinat: (" + (int)node.getX() + ", " + (int)node.getY() + ")";

        alert.setContentText(info);
        alert.showAndWait();
    }

    // Pencere boyutu değişince çizimi yenilemek için
    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void drawGraph(Graph graph) {
        this.currentGraph = graph; // Son grafiği kaydet
        GraphicsContext gc = getGraphicsContext2D();

        // 1. Temizlik
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Graf boşsa (null) çizim yapma
        if (graph == null) return;

        // 2. Kenarları Çiz
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getNeighbors(node)) {
                Node target = edge.getTarget();
                gc.strokeLine(node.getX(), node.getY(), target.getX(), target.getY());

                // Ağırlık yazısı
                double midX = (node.getX() + target.getX()) / 2;
                double midY = (node.getY() + target.getY()) / 2;
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(String.format("%.1f", edge.getWeight()), midX, midY);
            }
        }

        // 3. Düğümleri Çiz
        for (Node node : graph.getAllNodes()) {
            drawNode(gc, node);
        }
    }

    // Ekran yeniden boyutlandığında otomatik çağrılır (Main'de bind edince)
    public void redraw() {
        if (currentGraph != null) {
            drawGraph(currentGraph);
        }
    }

    private void drawNode(GraphicsContext gc, Node node) {
        double r = 20;
        gc.setFill(node.getColor());
        gc.fillOval(node.getX() - r, node.getY() - r, r * 2, r * 2);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(node.getX() - r, node.getY() - r, r * 2, r * 2);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(node.getName(), node.getX() - 10, node.getY() - 25);
    }
}