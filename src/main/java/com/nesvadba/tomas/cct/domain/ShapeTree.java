package com.nesvadba.tomas.cct.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.enums.ComponentProperty;

import ij.process.ImageProcessor;

public class ShapeTree {

    private int level;
    private int label;

    private Set<Point> points = new HashSet<>();
    private List<ShapeTree> nodes = new ArrayList<>();
    
    private ImageProcessor imageProcessor;
    
    private CCT origNode;
        
    public CCT getOrigNode() {
        return origNode;
    }

    public void setOrigNode(CCT origNode) {
        this.origNode = origNode;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    private Map<ComponentProperty, Integer> properties = new HashMap<>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public Set<Point> getPoints() {
        return points;
    }

    public void setPoints(Set<Point> points) {
        this.points = points;
    }

    public List<ShapeTree> getNodes() {
        return nodes;
    }

    public void setNodes(List<ShapeTree> nodes) {
        this.nodes = nodes;
    }

    public Map<ComponentProperty, Integer> getProperties() {
        return properties;
    }

    public void setProperties(Map<ComponentProperty, Integer> properties) {
        this.properties = properties;
    }

    // Comparator for Priority Que
    public static Comparator<ShapeTree> getComparator() {

        return new Comparator<ShapeTree>() {

            @Override
            public int compare(ShapeTree o1, ShapeTree o2) {

                return o2.getProperties().get(ComponentProperty.SIZE) - o1.getProperties().get(ComponentProperty.SIZE);

            }
        };

    }

    @Override
    public String toString() {
        return "ShapeTree [level=" + level + ", label=" + label + ", points=" + points + ", nodes=" + nodes + ", properties=" + properties + "]";
    }
    
    public void print(String str) {
        String msg = str+origNode.getCode() + "$"+properties+"& ->";
        System.out.println(msg);
        if (!nodes.isEmpty()) {
            for (ShapeTree node : nodes) {
                node.print(msg);
            }
        }
    }
    
    

}
