package com.nesvadba.tomas.cct.gui;

import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.RandomColor;
import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;
import com.nesvadba.tomas.cct.filter.CCTFilter;
import com.nesvadba.tomas.cct.filter.ShapeTreeFilter;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Painter {

    private ImagePlus image;
    private ImagePlus orig;

    public Painter(ImagePlus imageIn, ImagePlus origIn) {
        image = imageIn;
        orig = origIn;
    }

    private RandomColor randomColor = new RandomColor();

    public void filterShapeTreeImage(ShapeTree shapeTree, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {
        boolean isAnyFilterOn = false;
        for (boolean filterOn : selectedFilters.values()) {
            isAnyFilterOn = isAnyFilterOn || filterOn;
        }
        if (!isAnyFilterOn) {
            return;
        }

        reprintOrig(image, orig);
        ImageProcessor proc = image.getProcessor();

        image.repaintWindow();

        for (ShapeTree node : ShapeTreeFilter.filterByProperties(shapeTree, filterProperties, selectedFilters, keepOnlyOne)) {
            if (node==null) return ;
            proc.setColor(randomColor.getRandColor());

            int left = node.getOrigNode().getProperties().get(ComponentProperty.LEFT);
            int up = node.getOrigNode().getProperties().get(ComponentProperty.UP);
            int down = node.getOrigNode().getProperties().get(ComponentProperty.DOWN);
            int right = node.getOrigNode().getProperties().get(ComponentProperty.RIGHT);


            ImageProcessor mask = node.getImageProcessor();
            for (int x = 0; x < mask.getWidth(); x++) {
                for (int y = 0; y < mask.getHeight(); y++) {
                    if (mask.get(x, y) < 127) {
                        proc.drawDot(x + up-1, y + left-1);//-1 is BORDER 
                    }

                }
            }
//            proc.drawLine(down, left, up, left);
//            proc.drawLine(down, right, up, right);
//            
//            proc.drawLine(down, left, down, right);
//            proc.drawLine(up, left, up, right);

            image.repaintWindow();
        }

    }

    public void filterCCTImage(CCT cct, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {

        boolean isAnyFilterOn = false;
        for (boolean filterOn : selectedFilters.values()) {
            isAnyFilterOn = isAnyFilterOn || filterOn;
        }
        if (!isAnyFilterOn) {
            return;
        }

        reprintOrig(image, orig);
        ImageProcessor proc = image.getProcessor();

        image.repaintWindow();

        for (CCT node : CCTFilter.filterByProperties(cct, filterProperties, selectedFilters, keepOnlyOne)) {
            if (node==null) return ;
            proc.setColor(randomColor.getRandColor());
            for (Set<Point> subnode : node.getAllPoints()) {
                for (Point point : subnode) {
                    proc.drawDot(point.y, point.x);
                }
            }
            image.repaintWindow();
        }
    }
    
    
    public void reprintOrig(){
        reprintOrig(image, orig);
    }

    private void reprintOrig(ImagePlus image, ImagePlus orig) {

        ImageProcessor proc = image.getProcessor();
        ImageProcessor origProc = orig.getProcessor();
        // Reprint Orig
        for (int x = 0; x < proc.getWidth(); x++) {
            for (int y = 0; y < proc.getHeight(); y++) {
                int gray = origProc.get(x, y);
                proc.setColor(gray);
                proc.drawDot(x, y);
            }
        }

        image.repaintWindow();
    }
}
