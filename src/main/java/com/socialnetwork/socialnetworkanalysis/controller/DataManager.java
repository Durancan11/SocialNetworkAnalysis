package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager {

    // --- KAYDETME (SAVE) - GÜNCELLENDİ: KOMŞULAR SÜTUNU EKLENDİ ---
    public void saveGraph(Graph graph, String folderPath) {
        try {
            // 1. Düğümler (nodes.csv)
            // PDF İsteri: Çıktıda komşuluk listesi olmalı.
            BufferedWriter nodeWriter = new BufferedWriter(new FileWriter(folderPath + "/nodes.csv"));
            nodeWriter.write("id,name,x,y,activity,interaction,connectionCount,neighbors\n");

            for (Node node : graph.getAllNodes()) {
                // Komşuların ID'lerini topla (Örn: "2;5;9")
                // CSV'de karışıklık olmasın diye ID'leri noktalı virgül (;) ile ayırıyorum
                String neighborIds = graph.getNeighbors(node).stream()
                        .map(edge -> edge.getTarget().getId())
                        .collect(Collectors.joining(";"));

                if (neighborIds.isEmpty()) neighborIds = "None";

                String line = String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s",
                        node.getId(), node.getName(),
                        node.getX(), node.getY(),
                        node.getActivity(), node.getInteraction(), node.getConnectionCount(),
                        neighborIds);
                nodeWriter.write(line + "\n");
            }
            nodeWriter.close();

            // 2. Kenarları (edges.csv) - Aynen kalabilir
            BufferedWriter edgeWriter = new BufferedWriter(new FileWriter(folderPath + "/edges.csv"));
            edgeWriter.write("sourceId,targetId,weight\n");

            for (Node node : graph.getAllNodes()) {
                for (Edge edge : graph.getNeighbors(node)) {
                    // Çift kaydı önle
                    if (edge.getSource().getId().compareTo(edge.getTarget().getId()) < 0) {
                        edgeWriter.write(edge.getSource().getId() + "," + edge.getTarget().getId() + "," + edge.getWeight() + "\n");
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
        List<Node> loadedNodes = new ArrayList<>();

        try {
            // 1. Düğümleri Oku
            File nodeFile = new File(folderPath + "/nodes.csv");
            if (!nodeFile.exists()) return graph;

            BufferedReader nodeReader = new BufferedReader(new FileReader(nodeFile));
            String line = nodeReader.readLine(); // Başlığı atla

            while ((line = nodeReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) { // En az 7 veri lazım, neighbors opsiyonel okuyoruz
                    String id = parts[0];
                    String name = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    double activity = Double.parseDouble(parts[4]);
                    double interaction = Double.parseDouble(parts[5]);
                    double connCount = Double.parseDouble(parts[6]);

                    Node node = new Node(id, name, activity, interaction, connCount);

                    // --- DÜZELTME: Konum Ayarı ---
                    if (x == 0 && y == 0) {
                        setSafeRandomPosition(node, loadedNodes);
                    } else {
                        node.setX(x);
                        node.setY(y);
                        // Çarpışma varsa kaydır
                        while (isOverlapping(node, loadedNodes)) {
                            node.setX(node.getX() + 20);
                            node.setY(node.getY() + 20);
                        }
                    }

                    graph.addNode(node);
                    tempNodes.put(id, node);
                    loadedNodes.add(node);
                }
            }
            nodeReader.close();

            // 2. Kenarları Oku (Ağırlıklı)
            File edgeFile = new File(folderPath + "/edges.csv");
            if (edgeFile.exists()) {
                BufferedReader edgeReader = new BufferedReader(new FileReader(edgeFile));
                line = edgeReader.readLine();

                while ((line = edgeReader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String sourceId = parts[0];
                        String targetId = parts[1];

                        // Eğer dosyada ağırlık varsa oku, yoksa hesapla
                        double weight = 0;
                        if (parts.length >= 3) {
                            weight = Double.parseDouble(parts[2]);
                        }

                        Node source = tempNodes.get(sourceId);
                        Node target = tempNodes.get(targetId);

                        if (source != null && target != null) {
                            if (weight > 0) {
                                graph.addEdge(source, target, weight);
                            } else {
                                // Eski dosyalardan yüklüyorsak formülü burada da çalıştırabiliriz
                                // Ama şimdilik basit ekle
                                graph.addEdge(source, target, 1.0);
                            }
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

    private void setSafeRandomPosition(Node newNode, List<Node> existingNodes) {
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            double newX = Math.random() * 600 + 50;
            double newY = Math.random() * 400 + 50;
            newNode.setX(newX);
            newNode.setY(newY);
            if (!isOverlapping(newNode, existingNodes)) return;
        }
    }

    private boolean isOverlapping(Node n1, List<Node> nodes) {
        double minDistance = 50.0;
        for (Node n2 : nodes) {
            if (n1 == n2) continue;
            double dist = Math.sqrt(Math.pow(n1.getX() - n2.getX(), 2) + Math.pow(n1.getY() - n2.getY(), 2));
            if (dist < minDistance) return true;
        }
        return false;
    }
}