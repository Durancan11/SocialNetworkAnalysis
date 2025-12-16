package com.socialnetwork.socialnetworkanalysis.algorithms;

import com.socialnetwork.socialnetworkanalysis.model.Graph; // Artık Graph var!
import com.socialnetwork.socialnetworkanalysis.model.Node;

public interface GraphAlgorithm {
    String getName();

    // DİKKAT: Object yerine Graph yazdık
    void execute(Graph graph, Node startNode);
}