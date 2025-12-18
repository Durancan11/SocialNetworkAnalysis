package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    // --- KAYDETME (SAVE) ---
    public void saveGraph(Graph graph, String folderPath) {
        try {
            // 1. Düğümler (nodes.csv)
            BufferedWriter nodeWriter = new BufferedWriter(new FileWriter(folderPath + "/nodes.csv"));
            nodeWriter.write("id,name,x,y,activity,interaction,connectionCount\n");

            for (Node node : graph.getAllNodes()) {
                String line = String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f",
                        node.getId(), node.getName(),
                        node.getX(), node.getY(),
                        node.getActivity(), node.getInteraction(), node.getConnectionCount());
                nodeWriter.write(line + "\n");
            }
            nodeWriter.close();

            // 2. Kenarlar (edges.csv)
            BufferedWriter edgeWriter = new BufferedWriter(new FileWriter(folderPath + "/edges.csv"));
            edgeWriter.write("sourceId,targetId\n");

            for (Node node : graph.getAllNodes()) {
                for (Edge edge : graph.getNeighbors(node)) {
                    // Çift kaydı önle
                    if (edge.getSource().getId().compareTo(edge.getTarget().getId()) < 0) {
                        edgeWriter.write(edge.getSource().getId() + "," + edge.getTarget().getId() + "\n");
                    }
                }
            }
            edgeWriter.close();
            System.out.println("Kayıt Başarılı.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- YÜKLEME (LOAD) - ÇAKIŞMA ÖNLEYİCİ SÜRÜM ---
    public Graph loadGraph(String folderPath) {
        Graph graph = new Graph();
        Map<String, Node> tempNodes = new HashMap<>();
        List<Node> loadedNodes = new ArrayList<>(); // Konum kontrolü için liste

        try {
            // 1. Düğümleri Oku
            File nodeFile = new File(folderPath + "/nodes.csv");
            if (!nodeFile.exists()) return graph;

            BufferedReader nodeReader = new BufferedReader(new FileReader(nodeFile));
            String line = nodeReader.readLine(); // Başlığı atla

            while ((line = nodeReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String id = parts[0];
                    String name = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    double activity = Double.parseDouble(parts[4]);
                    double interaction = Double.parseDouble(parts[5]);
                    double connCount = Double.parseDouble(parts[6]);

                    Node node = new Node(id, name, activity, interaction, connCount);

                    // --- DÜZELTME 1: Sadece koordinatı olmayanları (0,0) rastgele at ---
                    // Artık her seferinde yer değiştirmeyecek!
                    if (x == 0 && y == 0) {
                        setSafeRandomPosition(node, loadedNodes);
                    } else {
                        // Kayıtlı konumu kullan ama üst üste binmişse hafif kaydır
                        node.setX(x);
                        node.setY(y);
                        // Eğer bu konumda başka biri varsa, çok az kaydır ki üst üste binmesin
                        while (isOverlapping(node, loadedNodes)) {
                            node.setX(node.getX() + 20); // Biraz sağa
                            node.setY(node.getY() + 20); // Biraz aşağı
                        }
                    }

                    graph.addNode(node);
                    tempNodes.put(id, node);
                    loadedNodes.add(node);
                }
            }
            nodeReader.close();

            // 2. Kenarları Oku
            File edgeFile = new File(folderPath + "/edges.csv");
            if (edgeFile.exists()) {
                BufferedReader edgeReader = new BufferedReader(new FileReader(edgeFile));
                line = edgeReader.readLine();

                while ((line = edgeReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String sourceId = parts[0];
                        String targetId = parts[1];
                        Node source = tempNodes.get(sourceId);
                        Node target = tempNodes.get(targetId);
                        if (source != null && target != null) {
                            graph.addEdge(source, target);
                        }
                    }
                }
                edgeReader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    // GÜVENLİ KONUM BULUCU (Üst üste binmeyi engeller)
    private void setSafeRandomPosition(Node newNode, List<Node> existingNodes) {
        int maxAttempts = 100; // Sonsuz döngüye girmesin
        for (int i = 0; i < maxAttempts; i++) {
            // Rastgele bir yer seç
            double newX = Math.random() * 600 + 50;
            double newY = Math.random() * 400 + 50;

            newNode.setX(newX);
            newNode.setY(newY);

            // Çarpışma var mı kontrol et
            if (!isOverlapping(newNode, existingNodes)) {
                return; // Yer güvenli, çık.
            }
        }
    }

    // İki düğüm üst üste mi? (Mesafeye bakar)
    private boolean isOverlapping(Node n1, List<Node> nodes) {
        double minDistance = 50.0; // Dairelerin birbirine girebileceği en yakın mesafe
        for (Node n2 : nodes) {
            if (n1 == n2) continue; // Kendisiyle kıyaslama

            double dist = Math.sqrt(Math.pow(n1.getX() - n2.getX(), 2) + Math.pow(n1.getY() - n2.getY(), 2));
            if (dist < minDistance) {
                return true; // Çarpışma var!
            }
        }
        return false;
    }
}