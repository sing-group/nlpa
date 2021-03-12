/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author María Novo
 */
public class SynsetNodeBuilder {

    public static List<SynsetNode> buildTrees(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            List<SynsetNode> nodes = new ArrayList<>();
            HashMap<String, SynsetInstance> instanceFlyweight = new HashMap<>();

            String line;
            SynsetNode currentNode = null;
            int previousTabs = -1;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (currentNode != null) {
                        nodes.add(currentNode.getRoot());
                        currentNode = null;
                        previousTabs = -1;
                    }
                } else {
                    int currentTabs = countTabs(line);
                    SynsetNode node = buildNode(line, instanceFlyweight);

                    if (currentTabs > previousTabs) {
                        if (currentTabs - previousTabs != 1) {
                            throw new IllegalStateException("Illegal tab increment");
                        } else {
                            if (currentNode != null) {
                                currentNode.addChild(node);
                            }
                        }
                    } else if (currentTabs < previousTabs) {
                        for (int i = 0; i < previousTabs - currentTabs; i++) {
                            currentNode = currentNode.getParent();
                        }

                        currentNode.getParent().addChild(node);
                    } else {
                        currentNode.getParent().addChild(node);
                    }

                    currentNode = node;
                    previousTabs = currentTabs;
                }
            }

            if (currentNode != null) {
                nodes.add(currentNode.getRoot());
            }

            return nodes;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static int countTabs(String line) {
        int count = 0;

        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\t') {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    private static SynsetNode buildNode(String line, Map<String, SynsetInstance> instanceFlyweight) {
        line = line.trim();

        String[] parts = line.split("\\|");
        String[] synsets = parts[0].split(",");
        String[] instances = parts.length == 2 ? parts[1].split(",") : new String[0];

        SynsetNode node = new SynsetNode(Arrays.asList(synsets));
        for (String instanceData : instances) {
            instanceData = instanceData.trim();

            final String[] nameAndTarget = instanceData.split(":");

            SynsetInstance instance = instanceFlyweight.computeIfAbsent(nameAndTarget[0],
                    name -> new BasicSynsetInstance(name, nameAndTarget[1]));

            if (!instance.getTarget().equals(nameAndTarget[1])) {
                throw new IllegalStateException("Instance found with different targets: " + nameAndTarget[0]);
            }

            node.addInstance(instance);
        }

        return node;
    }

    public static class BasicSynsetInstance implements SynsetInstance {

        private final String name;
        private final String target;

        public BasicSynsetInstance(String name, String target) {
            super();
            this.name = name;
            this.target = target;
        }

        @Override
        public Serializable getTarget() {
            return this.target;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
