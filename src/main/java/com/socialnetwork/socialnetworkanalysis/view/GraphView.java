package com.socialnetwork.socialnetworkanalysis.view;

import com.socialnetwork.socialnetworkanalysis.model.Edge;
import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GraphView extends Canvas {

    // Çizim alanı boyutları
    public GraphView(double width, double height) {
        super(width, height);
    }

    // Bu metod dışarıdan çağrılacak ve grafı çizecek
    public void drawGraph(Graph graph) {
        GraphicsContext gc = getGraphicsContext2D();

        // 1. Temizlik: Önce ekranı temizle (Eski çizimler gitsin)
        gc.clearRect(0, 0, getWidth(), getHeight());

        // 2. Kenarları (Çizgileri) Çiz
        // Önce çizgileri çiziyoruz ki dairelerin altında kalsın, şık dursun.
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        for (Node node : graph.getAllNodes()) {
            for (Edge edge : graph.getNeighbors(node)) {
                Node target = edge.getTarget();
                // Çizgi çiz: Kaynak X,Y -> Hedef X,Y
                gc.strokeLine(node.getX(), node.getY(), target.getX(), target.getY());

                // Ağırlığı çizginin ortasına yaz (İsteğe bağlı, kontrol için iyi olur)
                double midX = (node.getX() + target.getX()) / 2;
                double midY = (node.getY() + target.getY()) / 2;
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(String.format("%.1f", edge.getWeight()), midX, midY);
            }
        }

        // 3. Düğümleri (Daireleri) Çiz
        for (Node node : graph.getAllNodes()) {
            drawNode(gc, node);
        }
    }

    private void drawNode(GraphicsContext gc, Node node) {
        double r = 20; // Daire yarıçapı

        // Dairenin içini boya (Mavi)
        gc.setFill(Color.CORNFLOWERBLUE);
        // Daireyi merkeze oturtmak için x-r, y-r yapıyoruz
        gc.fillOval(node.getX() - r, node.getY() - r, r * 2, r * 2);

        // Dairenin çerçevesini çiz (Siyah)
        gc.setStroke(Color.BLACK);
        gc.strokeOval(node.getX() - r, node.getY() - r, r * 2, r * 2);

        // İsim etiketini yaz
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Arial", 12));
        gc.fillText(node.getName(), node.getX() - 10, node.getY() - 25);
    }
}