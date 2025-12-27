package com.socialnetwork.socialnetworkanalysis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    // Tüm düğümleri ID'lerine göre saklar (Hızlı erişim için)
    private Map<String, Node> nodes;

    // Komşuluk Listesi: Hangi düğüm, kimlere bağlı?
    // Anahtar: Düğüm, Değer: O düğümden çıkan kenarlar listesi
    private Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    // Sisteme yeni bir kullanıcı (düğüm) ekler
    public void addNode(Node node) {
        nodes.putIfAbsent(node.getId(), node);
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    // --- GÜNCELLENEN METOD: AĞIRLIKLI VE YÖNSÜZ EKLEME ---
    public void addEdge(Node source, Node target, double weight) {
        // PDF Madde 3.1: "Bağlantıların yönsüz olması sağlanmalıdır"
        // Bu yüzden A'dan B'ye eklerken, B'den de A'ya ekliyoruz.

        // 1. A -> B (Ağırlıklı)
        Edge edge1 = new Edge(source, target, weight);
        adjacencyList.get(source).add(edge1);

        // 2. B -> A (Aynı Ağırlıkla)
        Edge edge2 = new Edge(target, source, weight);
        adjacencyList.get(target).add(edge2);
    }

    // (Eski metod varsa sil veya overload olarak kalsın ama yenisini kullanacağız)
    public void addEdge(Node source, Node target) {
        addEdge(source, target, 1.0); // Varsayılan ağırlık 1
    }

    // ID'si verilen düğümü bulur
    public Node getNode(String id) {
        return nodes.get(id);
    }

    // Tüm düğümleri listeler
    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    // Bir düğümün komşularını (kenarlarını) getirir
    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    // Grafı temizle (Yeni dosya yüklerken gerekecek)
    public void clear() {
        nodes.clear();
        adjacencyList.clear();
    }

    // --- YENİ EKLENEN: SİLME İŞLEMLERİ (PDF Madde 3.1 İsteri) ---
    public void removeNode(Node node) {
        // 1. Önce bu düğüme gelen tüm bağlantıları diğerlerinin listesinden sil
        for (Node other : nodes.values()) {
            List<Edge> edges = adjacencyList.get(other);
            if (edges != null) {
                edges.removeIf(e -> e.getTarget().equals(node));
            }
        }
        // 2. Düğümü ve kendi listesini sil
        adjacencyList.remove(node);
        nodes.remove(node.getId());
    }

    // --- YENİ EKLENEN: BAĞLANTI SİLME METODU ---
    // PDF Madde 3.1: "Bağlantı silme yapılabilmelidir."
    public void removeEdge(Node source, Node target) {
        // Kaynaktan Hedefe gideni sil
        if (adjacencyList.containsKey(source)) {
            adjacencyList.get(source).removeIf(edge -> edge.getTarget().equals(target));
        }

        // Hedeftan Kaynağa gideni sil (Yönsüz olduğu için iki tarafı da temizliyoruz)
        if (adjacencyList.containsKey(target)) {
            adjacencyList.get(target).removeIf(edge -> edge.getTarget().equals(source));
        }
    }
}