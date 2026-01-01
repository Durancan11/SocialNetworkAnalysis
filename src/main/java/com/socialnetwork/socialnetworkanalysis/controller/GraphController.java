package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.algorithms.*;
import com.socialnetwork.socialnetworkanalysis.model.*;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;

public class GraphController {
    private Graph graph;
    private GraphView view;

    public GraphController(Graph graph, GraphView view) {
        this.graph = graph;
        this.view = view;
        this.view.setController(this);
    }

    // --- YARDIMCI METOD: AĞIRLIK FORMÜLÜ ---
    private double calculateWeight(Node n1, Node n2) {
        double diffAct = n1.getActivity() - n2.getActivity();
        double diffInt = n1.getInteraction() - n2.getInteraction();
        double diffConn = n1.getConnectionCount() - n2.getConnectionCount();
        return 1 + Math.sqrt(Math.pow(diffAct, 2) + Math.pow(diffInt, 2) + Math.pow(diffConn, 2));
    }

    // Düğüm Ekleme
    public void addNode(String name, double activity, double interaction, double connection) {
        if (findNodeByName(name) != null) {
            showAlert("Hata", name + " isimli kullanıcı zaten mevcut!"); return;
        }
        String id = String.valueOf(graph.getAllNodes().size() + 1);
        Node node = new Node(id, name, activity, interaction, connection);
        setRandomPositionSafe(node);
        graph.addNode(node);
        view.drawGraph(graph);
        System.out.println("Eklendi: " + name);
    }

    // --- TEST GRAFI OLUŞTURUCU (RASTGELE DAĞILIM) ---
    public void generateRandomGraph(int nodeCount) {
        clearGraph();
        Random random = new Random();
        System.out.println("Test Grafı Oluşturuluyor: " + nodeCount + " düğüm...");

        // Ekran genişliğini alalım (Varsayılan 800x600)
        double width = (view.getWidth() > 0) ? view.getWidth() : 800;
        double height = (view.getHeight() > 0) ? view.getHeight() : 600;

        for (int i = 1; i <= nodeCount; i++) {
            String name = "User_" + i;
            double act = Math.round(random.nextDouble() * 10) / 10.0;
            double intel = Math.round(random.nextDouble() * 10) / 10.0;
            double conn = random.nextInt(100);
            Node node = new Node(String.valueOf(i), name, act, intel, conn);

            // GÜNCELLEME: Çember yerine RASTGELE konuma geri döndük
            // Kenarlardan 50px pay bırakarak ekrana yayıyoruz
            node.setX(random.nextDouble() * (width - 100) + 50);
            node.setY(random.nextDouble() * (height - 100) + 50);

            graph.addNode(node);
        }

        // Rastgele Bağlantılar
        List<Node> nodes = graph.getAllNodes();
        for (Node source : nodes) {
            int connectionCount = random.nextInt(3) + 1;
            for (int k = 0; k < connectionCount; k++) {
                Node target = nodes.get(random.nextInt(nodes.size()));
                if (!source.equals(target)) {
                    double w = calculateWeight(source, target);
                    graph.addEdge(source, target, w);
                }
            }
        }
        view.drawGraph(graph);
    }

    // --- OTOMATİK DÜZENLEME (Force-Directed) ---
    public void runSpringLayout() {
        if (graph.getAllNodes().isEmpty()) return;

        System.out.println("Düzenleme Hesaplanıyor...");

        double width = (view.getWidth() > 0) ? view.getWidth() : 800;
        double height = (view.getHeight() > 0) ? view.getHeight() : 600;

        int iterations = 100;
        double area = width * height;
        // İdeal mesafe
        double k = Math.sqrt(area / graph.getAllNodes().size()) * 1.5;
        double temperature = width / 10;

        for (int i = 0; i < iterations; i++) {
            // 1. İtme Kuvveti
            for (Node n1 : graph.getAllNodes()) {
                double dispX = 0, dispY = 0;
                for (Node n2 : graph.getAllNodes()) {
                    if (n1 == n2) continue;

                    double dx = n1.getX() - n2.getX();
                    double dy = n1.getY() - n2.getY();
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    if (dist < 1) dist = 1;

                    double force = (k * k) / dist;
                    dispX += (dx / dist) * force;
                    dispY += (dy / dist) * force;
                }
                n1.setDx(dispX);
                n1.setDy(dispY);
            }

            // 2. Çekme Kuvveti
            for (Node n : graph.getAllNodes()) {
                for (Edge edge : graph.getNeighbors(n)) {
                    Node target = edge.getTarget();
                    double dx = n.getX() - target.getX();
                    double dy = n.getY() - target.getY();
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    if (dist < 1) dist = 1;

                    // Daha yumuşak çekim kuvveti (dist^2 yerine dist)
                    double force = (dist * dist) / k;

                    double dispX = (dx / dist) * force;
                    double dispY = (dy / dist) * force;

                    n.setDx(n.getDx() - dispX);
                    n.setDy(n.getDy() - dispY);
                }
            }

            // 3. Konum Güncelleme
            for (Node n : graph.getAllNodes()) {
                double dx = n.getDx();
                double dy = n.getDy();
                double dist = Math.sqrt(dx*dx + dy*dy);

                if (dist > 0) {
                    double move = Math.min(dist, temperature);
                    n.setX(n.getX() + (dx / dist) * move);
                    n.setY(n.getY() + (dy / dist) * move);
                }

                // Sınırlar
                n.setX(Math.min(width - 50, Math.max(50, n.getX())));
                n.setY(Math.min(height - 50, Math.max(50, n.getY())));
            }
            temperature *= 0.90;
        }
        view.drawGraph(graph);
        System.out.println("Düzenleme Bitti.");
    }

    // Güvenli Rastgele Konum
    private void setRandomPositionSafe(Node node) {
        boolean safe = false; int attempts=0;
        while(!safe && attempts<100) {
            double tx=Math.random()*650+50, ty=Math.random()*450+50;
            boolean col=false;
            for(Node ex:graph.getAllNodes()) if(Math.sqrt(Math.pow(tx-ex.getX(),2)+Math.pow(ty-ex.getY(),2))<60) {col=true; break;}
            if(!col) { node.setX(tx); node.setY(ty); safe=true; } attempts++;
        }
        if(!safe) { node.setX(Math.random()*600+50); node.setY(Math.random()*400+50); }
    }

    public void addEdge(String sourceName, String targetName) {
        if (sourceName.equalsIgnoreCase(targetName)) { showAlert("Hata", "Self-Loop yasak!"); return; }
        Node s = findNodeByName(sourceName), t = findNodeByName(targetName);
        if (s!=null && t!=null) { double w=calculateWeight(s,t); graph.addEdge(s,t,w); view.drawGraph(graph); }
        else showAlert("Hata", "Kullanıcı bulunamadı.");
    }

    public void removeEdge(String sName, String tName) {
        Node s=findNodeByName(sName), t=findNodeByName(tName);
        if(s!=null && t!=null) { graph.removeEdge(s,t); view.drawGraph(graph); showAlert("Başarılı","Bağ koparıldı."); }
        else showAlert("Hata","Bulunamadı.");
    }

    public void updateNode(Node n, double a, double i, double c) {
        n.setActivity(a); n.setInteraction(i); n.setConnectionCount(c);
        for(Edge e:graph.getNeighbors(n)) {
            double w=calculateWeight(n,e.getTarget()); e.setWeight(w);
            for(Edge re:graph.getNeighbors(e.getTarget())) if(re.getTarget().equals(n)) re.setWeight(w);
        }
        view.drawGraph(graph);
    }

    private Node findNodeByName(String name) {
        for (Node node : graph.getAllNodes()) if (node.getName().equalsIgnoreCase(name)) return node;
        return null;
    }

    // --- ALGORİTMALAR ---
    public void runBFS(String s) {
        Node n=findNodeByName(s); if(n==null){showAlert("Hata","Başlangıç yok");return;}
        long t=measure(()->new BFSAlgorithm().execute(graph,n)); showAlert("BFS","Süre: "+t+" µs");
    }
    public void runDFS(String s) {
        Node n=findNodeByName(s); if(n==null){showAlert("Hata","Başlangıç yok");return;}
        long t=measure(()->new DFSAlgorithm().execute(graph,n)); showAlert("DFS","Süre: "+t+" µs");
    }
    public void runDijkstra(String s) {
        Node n=findNodeByName(s); if(n==null){showAlert("Hata","Başlangıç yok");return;}
        long t=measure(()->new DijkstraAlgorithm().execute(graph,n)); showAlert("Dijkstra","Süre: "+t+" µs");
    }
    public void runAStar(String s, String t) {
        Node n1=findNodeByName(s), n2=findNodeByName(t); if(n1==null||n2==null){showAlert("Hata","Eksik düğüm");return;}
        long tm=measure(()->new AStarAlgorithm().findPath(graph,n1,n2)); showAlert("A*","Süre: "+tm+" µs");
    }
    //public void runCentrality() {
    //    long t=measure(()->new DegreeCentralityAlgorithm().execute(graph,null));
    //    List<Node> top=graph.getAllNodes().stream().sorted((a,b)->Double.compare(b.getConnectionCount(),a.getConnectionCount())).limit(5).collect(Collectors.toList());
    //    StringBuilder sb=new StringBuilder("EN POPÜLER 5:\n"); for(Node n:top) sb.append("- "+n.getName()+" ("+n.getConnectionCount()+")\n");
    //    sb.append("\nSüre: "+t+" µs"); showAlert("Merkezilik",sb.toString());
    //}
    // --- MERKEZİLİK ANALİZİ (DEGREE CENTRALITY & TOP 5 TABLE) ---
    public void runCentrality() {
        if (graph.getAllNodes().isEmpty()) {
            showAlert("Uyarı", "Analiz edilecek veri yok.");
            return;
        }

        // 1. Düğümleri Bağlantı Sayısına (Derece) göre sırala (Çoktan aza)
        List<Node> sortedNodes = new ArrayList<>(graph.getAllNodes());
        sortedNodes.sort((n1, n2) -> Integer.compare(
                graph.getNeighbors(n2).size(),
                graph.getNeighbors(n1).size()
        ));

        // 2. Görsel Efekt: En güçlüleri vurgula
        // Önce herkesi sıfırla (Varsayılan renk)
        for(Node n : graph.getAllNodes()) n.setColor(Color.web("#3498db")); // Varsayılan Mavi

        // En güçlü 5 kişiyi Altın Sarısı yap
        int topLimit = Math.min(5, sortedNodes.size());
        for(int i=0; i<topLimit; i++) {
            sortedNodes.get(i).setColor(Color.GOLD);
        }
        view.drawGraph(graph); // Grafiği güncelle

        // 3. RAPOR OLUŞTUR (PDF İsteri: Top 5 Tablosu)
        StringBuilder report = new StringBuilder();
        report.append("🏆 EN ETKİLİ 5 KULLANICI (DEGREE CENTRALITY)\n");
        report.append("──────────────────────────────────────────\n");
        report.append(String.format("%-5s %-15s %-10s\n", "SIRA", "KULLANICI ADI", "DERECE (SKOR)"));
        report.append("──────────────────────────────────────────\n");

        for (int i = 0; i < topLimit; i++) {
            Node n = sortedNodes.get(i);
            int degree = graph.getNeighbors(n).size();

            // Tablo formatında ekle
            report.append(String.format("%-5d %-15s %-10d\n", (i+1), n.getName(), degree));
        }

        // Eğer 5'ten fazla kişi varsa dipnot düş
        if (sortedNodes.size() > 5) {
            report.append("\n... ve ").append(sortedNodes.size() - 5).append(" kullanıcı daha.");
        }

        // 4. Sonucu Göster
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Merkezilik Analizi Raporu");
        alert.setHeaderText("Ağın Liderleri Belirlendi");

        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        // Düzen (Layout)
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    //public void runWelshPowell() {
    //    long t=measure(()->new WelshPowellAlgorithm().execute(graph,null));
    //    view.drawGraph(graph); showAlert("Renklendirme","Tamamlandı. Süre: "+t+" µs");
    //}
    // --- WELSH-POWELL RENKLENDİRME ALGORİTMASI ---
    public void runWelshPowell() {
        if (graph.getAllNodes().isEmpty()) {
            showAlert("Uyarı", "Graf boş, boyanacak düğüm yok.");
            return;
        }
        //Yapay zeka desteği alındı
        // Tüm düğümleri derecelerine (komşu sayılarına) göre BÜYÜKTEN KÜÇÜĞE sırala
        List<Node> sortedNodes = new ArrayList<>(graph.getAllNodes());

        sortedNodes.sort((n1, n2) -> Integer.compare(
                graph.getNeighbors(n2).size(),
                graph.getNeighbors(n1).size()
        ));

        // Renk paleti
        Color[] palette = {
                Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
                Color.PURPLE, Color.CYAN, Color.MAGENTA, Color.BROWN,
                Color.PINK, Color.LIME, Color.GOLD, Color.TEAL
        };

        Map<Node, Integer> nodeColors = new HashMap<>();
        for (Node n : sortedNodes) nodeColors.put(n, -1);

        int colorIndex = 0;

        // 2. Algoritma Döngüsü
        for (int i = 0; i < sortedNodes.size(); i++) {
            Node highestNode = sortedNodes.get(i);

            if (nodeColors.get(highestNode) == -1) {
                int currentColor = colorIndex;
                nodeColors.put(highestNode, currentColor);
                highestNode.setColor(palette[currentColor % palette.length]);

                for (int j = i + 1; j < sortedNodes.size(); j++) {
                    Node candidate = sortedNodes.get(j);

                    if (nodeColors.get(candidate) == -1) {
                        boolean isConnectedToCurrentColor = false;

                        // BURADA DA graph.getNeighbors() KULLANIYORUZ
                        for (Edge edge : graph.getNeighbors(candidate)) {
                            Node neighbor = edge.getTarget();
                            if (nodeColors.get(neighbor) == currentColor) {
                                isConnectedToCurrentColor = true;
                                break;
                            }
                        }

                        if (!isConnectedToCurrentColor) {
                            nodeColors.put(candidate, currentColor);
                            candidate.setColor(palette[currentColor % palette.length]);
                        }
                    }
                }
                colorIndex++;
            }
        }

        // 3. Görseli Güncelle
        view.drawGraph(graph);

        // 4. RAPOR OLUŞTUR
        showColoringTable(nodeColors, palette, colorIndex);
    }

    // boyama tablosu
    private void showColoringTable(Map<Node, Integer> mapping, Color[] palette, int totalColors) {
        StringBuilder report = new StringBuilder();
        report.append("🎨 KROMATİK SAYI (Kullanılan Renk Sayısı): ").append(totalColors).append("\n");
        report.append("──────────────────────────────────────────\n");

        for (int c = 0; c < totalColors; c++) {
            String colorName = getColorName(palette[c % palette.length]);
            report.append(String.format("RENK %d (%s): ", c + 1, colorName));

            List<String> nodesInThisColor = new ArrayList<>();
            for (Map.Entry<Node, Integer> entry : mapping.entrySet()) {
                if (entry.getValue() == c) {
                    nodesInThisColor.add(entry.getKey().getName());
                }
            }
            report.append(String.join(", ", nodesInThisColor)).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welsh-Powell Boyama Tablosu");
        alert.setHeaderText("Algoritma Tamamlandı");

        // TextArea Import edildiği için artık çalışacak
        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        // Layout ayarı
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    // Renklerin isimlerini yazıya dökmek için basit yardımcı
    private String getColorName(Color c) {
        if(c.equals(Color.RED)) return "Kırmızı";
        if(c.equals(Color.BLUE)) return "Mavi";
        if(c.equals(Color.GREEN)) return "Yeşil";
        if(c.equals(Color.ORANGE)) return "Turuncu";
        if(c.equals(Color.PURPLE)) return "Mor";
        if(c.equals(Color.CYAN)) return "Cam Göbeği";
        if(c.equals(Color.MAGENTA)) return "Eflatun";
        if(c.equals(Color.BROWN)) return "Kahverengi";
        return "Özel Renk";
    }
    //public void runConnectedComponents() {
    //    long t=measure(()->new ConnectedComponentsAlgorithm().execute(graph,null));
    //    showAlert("Topluluk","Tamamlandı. Süre: "+t+" µs");
    //}
    // --- BAĞLI BİLEŞENLER (TOPLULUK) TESPİTİ ---
    public void runConnectedComponents() {
        if (graph.getAllNodes().isEmpty()) {
            showAlert("Uyarı", "Analiz edilecek veri yok.");
            return;
        }

        // Ziyaret edilenleri takip et
        Set<Node> visited = new HashSet<>();
        List<List<Node>> components = new ArrayList<>();

        // BFS/DFS mantığıyla adaları (components) bul
        for (Node node : graph.getAllNodes()) {
            if (!visited.contains(node)) {
                // Yeni bir keşfedilmemiş ada bulduk!
                List<Node> component = new ArrayList<>();
                findComponentBFS(node, visited, component);
                components.add(component);
            }
        }

        // Renk Paleti (Her topluluk için farklı renk)
        Color[] palette = {
                Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
                Color.PURPLE, Color.CYAN, Color.MAGENTA, Color.BROWN
        };

        // Görseli Güncelle (Boyama)
        for (int i = 0; i < components.size(); i++) {
            Color c = palette[i % palette.length];
            for (Node n : components.get(i)) {
                n.setColor(c);
            }
        }
        view.drawGraph(graph);

        // RAPOR OLUŞTUR
        StringBuilder report = new StringBuilder();
        report.append("🧩 TOPLULUK ANALİZİ SONUCU\n");
        report.append("Tespit Edilen Ayrık Topluluk Sayısı: ").append(components.size()).append("\n");
        report.append("──────────────────────────────────────────\n");

        for (int i = 0; i < components.size(); i++) {
            report.append("GRUP ").append(i + 1).append(" (").append(getColorName(palette[i % palette.length])).append("): ");

            List<String> names = new ArrayList<>();
            for (Node n : components.get(i)) names.add(n.getName());

            report.append(String.join(", ", names)).append("\n");
        }

        // Sonucu Göster
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Topluluk Tespiti Raporu");
        alert.setHeaderText(components.size() + " Adet Ayrık Topluluk Bulundu");

        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    // Yardımcı Metod: Bir düğümden başlayıp bağlı olan herkesi bulur (BFS)
    private void findComponentBFS(Node startNode, Set<Node> visited, List<Node> component) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(startNode);
        visited.add(startNode);

        while(!queue.isEmpty()) {
            Node current = queue.poll();
            component.add(current);

            for(Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTarget();
                if(!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
    public void clearGraph() { graph.clear(); view.drawGraph(graph); showAlert("Bilgi", "Sistem sıfırlandı."); }

    private long measure(Runnable action) {
        long start = System.nanoTime(); action.run(); return (System.nanoTime() - start) / 1000;
    }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}