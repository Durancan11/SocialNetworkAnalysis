package com.socialnetwork.socialnetworkanalysis.controller;

import com.socialnetwork.socialnetworkanalysis.model.Graph;
import com.socialnetwork.socialnetworkanalysis.model.Node;
import com.socialnetwork.socialnetworkanalysis.model.Edge;

import java.io.*;
import java.util.HashMap;
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
                    // Çift kaydı önle (A-B varsa B-A yazma)
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

    // --- YÜKLEME (LOAD) - AKILLI SÜRÜM ---
    public Graph loadGraph(String folderPath) {
        Graph graph = new Graph();
        Map<String, Node> tempNodes = new HashMap<>();

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

                    // --- DÜZELTME OPERASYONU ---
                    // Eğer eski kayıt çok tepedeyse veya soldaysa (x veya y < 80),
                    // onları merkeze taşı.
                    if (y < 80 || x < 50) {
                        node.setX(Math.random() * 500 + 100);
                        node.setY(Math.random() * 300 + 100);
                    } else {
                        // Zaten düzgün yerdeyse elleme
                        node.setX(x);
                        node.setY(y);
                    }
                    // ---------------------------

                    graph.addNode(node);
                    tempNodes.put(id, node);
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
            System.out.println("Yükleme Başarılı.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Dosya okuma hatası!");
        }

        return graph;
    }
}