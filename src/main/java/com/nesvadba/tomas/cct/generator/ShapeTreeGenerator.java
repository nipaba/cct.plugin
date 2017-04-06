package com.nesvadba.tomas.cct.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.nesvadba.tomas.cct.Convertor;
import com.nesvadba.tomas.cct.Filling;
import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;

public class ShapeTreeGenerator {

    private static final int BORDER = 1;

    public ShapeTree createShapeTree(List<CCT> ccts) {

        System.out.println("ShapeTreeGenerator - Building Start");
        List<ShapeTree> shapeQue = new ArrayList<>();
        int counter = 0;
        int temp = 0;
        long start = System.currentTimeMillis();

        for (CCT cct : ccts) {
            System.out.println(" ------------------------" + cct.getName() + " --------------------------------");
            for (CCT node : cct.getAllNodes()) {

                long nodeStart = System.currentTimeMillis();
                counter++;
                if (counter / 10000 > temp) {
                    System.out.println("Counter :" + temp + " * 10 000 uzlu zpracované");
                    temp = counter / 10000;
                }
                ImageProcessor proc = Convertor.createBorderedImage(node, BORDER);

                Filling.fill(proc, 0, 255);

                ResultsTable rt = getStats(proc);

                ShapeTree shapeTree = createShapeThreeNode(rt, node.getLevel(), node);

                shapeTree.setLabel(counter);
                shapeTree.setImageProcessor(proc);

                shapeQue.add(shapeTree);

                long duration = System.currentTimeMillis() - nodeStart;
                if (duration > 100) {
                    System.out.println("ShapeTreeGenerator : createShapeTree := " + duration + "/" + node.getProperties());// TODO LOG REMOVE

                }
            }
        }

        IJ.log("QUE SIZE : " + shapeQue.size());

        // Build ShapeTree - size, contains build
        Collections.sort(shapeQue, ShapeTree.getComparator());

        ShapeTree root = buildShapeThree(shapeQue);
        // root.print("");

        System.out.println("ShapeTreeGenerator - Building finish with time :" + (System.currentTimeMillis() - start));
        return root;
    }

    private ShapeTree buildShapeThree(List<ShapeTree> shapeQue) {

        ShapeTree root = shapeQue.get(0);
        int counter = 1;
        for (int i = 1; i < shapeQue.size(); i++) {

            ShapeTree node = shapeQue.get(i);
            // System.out.println("ShapeTreeGenerator : buildShapeThree := " + node.getOrigNode().getProperties().get(ComponentProperty.SIZE)); // TODO LOG REMOVE
            Queue<ShapeTree> tempQue = new LinkedList<>();
            boolean isDuplicate = false;

            int childLeft = node.getOrigNode().getProperties().get(ComponentProperty.LEFT);
            int childRight = node.getOrigNode().getProperties().get(ComponentProperty.RIGHT);
            int childUp = node.getOrigNode().getProperties().get(ComponentProperty.UP);
            int childDown = node.getOrigNode().getProperties().get(ComponentProperty.DOWN);
            int childSize = node.getProperties().get(ComponentProperty.SIZE);
            ShapeTree parent = root;

            tempQue.add(root);

            while (!tempQue.isEmpty()) {

                ShapeTree searchedParent = tempQue.poll();

                int parentLeft = searchedParent.getOrigNode().getProperties().get(ComponentProperty.LEFT);
                int parentRight = searchedParent.getOrigNode().getProperties().get(ComponentProperty.RIGHT);
                int parentUp = searchedParent.getOrigNode().getProperties().get(ComponentProperty.UP);
                int parentDown = searchedParent.getOrigNode().getProperties().get(ComponentProperty.DOWN);
                int parentSize = searchedParent.getProperties().get(ComponentProperty.SIZE);
//                System.out.println("ShapeTreeGenerator : buildShapeThree := " + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); // TODO LOG REMOVE
//                System.out.println("ShapeTreeGenerator : buildShapeThree := " + "PARENT" + searchedParent.getProperties());
//                System.out.println("ShapeTreeGenerator : buildShapeThree := " + "NODE" + node.getProperties()); // TODO LOG REMOVE
                if (parentLeft <= childLeft && parentUp <= childUp && parentDown >= childDown && parentRight >= childRight) {
                    // CHILD
                    if (childSize == parentSize && parentLeft == childLeft && parentUp == childUp && parentDown == childDown && parentRight == childRight) {
                        // DUPLIKACE
                        // System.out.println((childSize == parentSize) + " " + searchedParent.toString() + ""+node.toString() );
                        isDuplicate = true;
                    } else {
                        // JSEM MENSI
                        parent = searchedParent;
                        tempQue.clear();
                        tempQue.addAll(searchedParent.getNodes());
                    }
                }
            }

            if (!isDuplicate) {
                parent.getNodes().add(node);
                node.setParentNode(parent);
                counter++;
                // root.print("");
            }

        }

        // root.print("");
        System.out.println("Counter:" + counter);
        return root;
    }

    private ResultsTable getStats(ImageProcessor proc) {
        int options = ParticleAnalyzer.SHOW_PROGRESS;
        int measurements = Measurements.ALL_STATS;
        int minSize = 1;
        int maxSize = Integer.MAX_VALUE;
        ResultsTable rt = new ResultsTable();
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, minSize, maxSize);
        pa.analyze(new ImagePlus("", proc));

        StringBuilder str = new StringBuilder();
        for (int i = 0; i <= rt.getLastColumn(); i++) {
            if (rt.columnExists(i)) {
                str.append(rt.getColumnHeading(i) + ":" + rt.getValueAsDouble(i, 0) + ",");
            }
        }

        return rt;
    }

    private ShapeTree createShapeThreeNode(ResultsTable rt, int level, CCT node) {

        ShapeTree shapeTree = new ShapeTree();
        int size = 0;
        int round, perim, elongation;
        // System.out.println("XXX" + rt.getColumnHeadings()) ;
        shapeTree.setLevel(level);
        shapeTree.setOrigNode(node);
        Map<ComponentProperty, Integer> props = shapeTree.getProperties();
        int index;

        props.put(ComponentProperty.LEFT, node.getProperties().get(ComponentProperty.LEFT));
        props.put(ComponentProperty.RIGHT, node.getProperties().get(ComponentProperty.RIGHT));
        props.put(ComponentProperty.UP, node.getProperties().get(ComponentProperty.UP));
        props.put(ComponentProperty.DOWN, node.getProperties().get(ComponentProperty.DOWN));

        props.put(ComponentProperty.INTENSITY, node.getProperties().get(ComponentProperty.INTENSITY));

        if ((index = rt.getColumnIndex("Area")) != ResultsTable.COLUMN_NOT_FOUND) {
            size = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
            props.put(ComponentProperty.SIZE, size);
        }

        if ((index = rt.getColumnIndex("Round")) != ResultsTable.COLUMN_NOT_FOUND) {
            round = Double.valueOf(rt.getValueAsDouble(index, 0) * 100).intValue();
            props.put(ComponentProperty.ROUND, round);
        }

        if ((index = rt.getColumnIndex("Perim.")) != ResultsTable.COLUMN_NOT_FOUND) {
            perim = Double.valueOf(rt.getValueAsDouble(index, 0)).intValue();
            props.put(ComponentProperty.PERIMETER, perim);

            elongation = (int) (100 * perim * perim / (4 * Math.PI * size));

            props.put(ComponentProperty.ELONGATION, elongation);
        }
        // System.out.println(shapeTree);
        return shapeTree;
    }

}
