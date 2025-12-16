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

    // İki kullanıcı arasına bağlantı ekler (Yönsüz olduğu için iki taraflı eklenir)
    public void addEdge(Node source, Node target) {
        // Eğer düğümler yoksa önce onları ekle
        addNode(source);
        addNode(target);

        // A -> B bağlantısı
        Edge edge1 = new Edge(source, target);
        adjacencyList.get(source).add(edge1);

        // B -> A bağlantısı (Yönsüz Graf kuralı gereği )
        Edge edge2 = new Edge(target, source);
        adjacencyList.get(target).add(edge2);
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
}