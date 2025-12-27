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

    // --- YENİ: RASTGELE TEST GRAFI OLUŞTURUCU (PDF Madde 4.5) ---
    public void generateRandomGraph(int nodeCount) {
        clearGraph(); // Önce temizle
        Random random = new Random();
        System.out.println("Test Grafı Oluşturuluyor: " + nodeCount + " düğüm...");

        // 1. Düğümleri Oluştur
        for (int i = 1; i <= nodeCount; i++) {
            String name = "User_" + i;
            double act = Math.round(random.nextDouble() * 10) / 10.0; // 0.0 - 1.0 arası
            double intel = Math.round(random.nextDouble() * 10) / 10.0;
            double conn = random.nextInt(100);

            Node node = new Node(String.valueOf(i), name, act, intel, conn);
            // Rastgele konum ata (Çakışma kontrolü yapmadan hızlıca dağıt, yoksa donar)
            node.setX(random.nextDouble() * 700 + 50);
            node.setY(random.nextDouble() * 500 + 50);
            graph.addNode(node);
        }

        // 2. Rastgele Bağlantılar Kur (Herkes en az 1-3 kişiye bağlansın)
        List<Node> nodes = graph.getAllNodes();
        for (Node source : nodes) {
            int connectionCount = random.nextInt(3) + 1; // 1 ile 3 arası bağlantı
            for (int k = 0; k < connectionCount; k++) {
                Node target = nodes.get(random.nextInt(nodes.size()));
                if (!source.equals(target)) { // Kendine bağlama
                    double w = calculateWeight(source, target);
                    graph.addEdge(source, target, w);
                }
            }
        }

        view.drawGraph(graph);
        showAlert("Test Ortamı Hazır", nodeCount + " kişilik rastgele ağ oluşturuldu!\nŞimdi algoritmaları test edebilirsin.");
    }

    // Güvenli Rastgele Konum (Tekil eklemeler için)
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

    // --- ALGORİTMALAR (SÜRE ÖLÇÜMLÜ - PDF Madde 4.5) ---

    public void runBFS(String s) {
        Node n = findNodeByName(s); if(n==null) {showAlert("Hata","Başlangıç yok"); return;}
        long start = System.nanoTime(); // Kronometre Başlat
        new BFSAlgorithm().execute(graph, n);
        long time = (System.nanoTime() - start) / 1000; // Mikrosaniye
        showAlert("BFS Tamamlandı", "İşlem Süresi: " + time + " µs (mikrosaniye)");
    }

    public void runDFS(String s) {
        Node n = findNodeByName(s); if(n==null) {showAlert("Hata","Başlangıç yok"); return;}
        long start = System.nanoTime();
        new DFSAlgorithm().execute(graph, n);
        long time = (System.nanoTime() - start) / 1000;
        showAlert("DFS Tamamlandı", "İşlem Süresi: " + time + " µs");
    }

    public void runDijkstra(String s) {
        Node n = findNodeByName(s); if(n==null) {showAlert("Hata","Başlangıç yok"); return;}
        long start = System.nanoTime();
        new DijkstraAlgorithm().execute(graph, n);
        long time = (System.nanoTime() - start) / 1000;
        showAlert("Dijkstra Tamamlandı", "İşlem Süresi: " + time + " µs");
    }

    public void runAStar(String s, String t) {
        Node n1=findNodeByName(s), n2=findNodeByName(t);
        if(n1==null||n2==null) {showAlert("Hata","Düğüm yok"); return;}
        long start = System.nanoTime();
        new AStarAlgorithm().findPath(graph, n1, n2);
        long time = (System.nanoTime() - start) / 1000;
        showAlert("A* Tamamlandı", "İşlem Süresi: " + time + " µs");
    }

    public void runCentrality() {
        long start = System.nanoTime();
        new DegreeCentralityAlgorithm().execute(graph, null);
        long time = (System.nanoTime() - start) / 1000;

        List<Node> top = graph.getAllNodes().stream()
                .sorted((a,b)->Double.compare(b.getConnectionCount(),a.getConnectionCount())).limit(5).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("EN POPÜLER 5 KİŞİ:\n");
        for(Node n : top) sb.append("- " + n.getName() + " (" + n.getConnectionCount() + ")\n");
        sb.append("\nAnaliz Süresi: " + time + " µs");
        showAlert("Merkezilik Sonucu", sb.toString());
    }

    public void runConnectedComponents() {
        long start = System.nanoTime();
        new ConnectedComponentsAlgorithm().execute(graph, null);
        long time = (System.nanoTime() - start) / 1000;
        showAlert("Topluluk Analizi", "Tamamlandı.\nSüre: " + time + " µs");
    }

    public void runWelshPowell() {
        long start = System.nanoTime();
        new WelshPowellAlgorithm().execute(graph, null);
        long time = (System.nanoTime() - start) / 1000;
        view.drawGraph(graph);
        showAlert("Renklendirme", "Tamamlandı.\nSüre: " + time + " µs");
    }

    public void clearGraph() { graph.clear(); view.drawGraph(graph); showAlert("Bilgi", "Sistem sıfırlandı."); }

    private void showAlert(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}