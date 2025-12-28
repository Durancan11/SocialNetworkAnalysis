package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.algorithms.*;
import com.socialnetwork.socialnetworkanalysis.model.*;
import com.socialnetwork.socialnetworkanalysis.view.GraphView;
import javafx.scene.control.Alert;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    public void runCentrality() {
        long t=measure(()->new DegreeCentralityAlgorithm().execute(graph,null));
        List<Node> top=graph.getAllNodes().stream().sorted((a,b)->Double.compare(b.getConnectionCount(),a.getConnectionCount())).limit(5).collect(Collectors.toList());
        StringBuilder sb=new StringBuilder("EN POPÜLER 5:\n"); for(Node n:top) sb.append("- "+n.getName()+" ("+n.getConnectionCount()+")\n");
        sb.append("\nSüre: "+t+" µs"); showAlert("Merkezilik",sb.toString());
    }
    public void runWelshPowell() {
        long t=measure(()->new WelshPowellAlgorithm().execute(graph,null));
        view.drawGraph(graph); showAlert("Renklendirme","Tamamlandı. Süre: "+t+" µs");
    }
    public void runConnectedComponents() {
        long t=measure(()->new ConnectedComponentsAlgorithm().execute(graph,null));
        showAlert("Topluluk","Tamamlandı. Süre: "+t+" µs");
    }
    public void clearGraph() { graph.clear(); view.drawGraph(graph); showAlert("Bilgi", "Sistem sıfırlandı."); }

    private long measure(Runnable action) {
        long start = System.nanoTime(); action.run(); return (System.nanoTime() - start) / 1000;
    }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}