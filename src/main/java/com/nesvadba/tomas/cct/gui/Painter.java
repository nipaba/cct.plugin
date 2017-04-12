package com.nesvadba.tomas.cct.gui;

import java.util.HashMap;
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

    private Map<ComponentProperty, Integer> lastFoundedProps;

    public Painter(ImagePlus imageIn, ImagePlus origIn) {
        image = imageIn;
        orig = origIn;
    }

    private RandomColor randomColor = new RandomColor();

    public void filterShapeTreeImage(ShapeTree shapeTree, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne, boolean similar,int procent) {
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

        Map<FilterProps, Integer> props = filterProperties;
        Map<ComponentProperty, Boolean> filters = selectedFilters;

        if (similar) {
            props = getSimilarProperties(filterProperties,procent);
            filters = getSimilarFilters(selectedFilters);
            keepOnlyOne = false;
            System.out.println("------------------");
            System.out.println(props);
            System.out.println(lastFoundedProps);
        }

        for (ShapeTree node : ShapeTreeFilter.filterByProperties(shapeTree, props, filters, keepOnlyOne)) {
            
            if (keepOnlyOne){
                lastFoundedProps= node.getProperties();
            }
            if (node == null)
                return;
            proc.setColor(randomColor.getRandColor());

            int left = node.getOrigNode().getProperties().get(ComponentProperty.LEFT);
            int up = node.getOrigNode().getProperties().get(ComponentProperty.UP);

            ImageProcessor mask = node.getImageProcessor();
            for (int x = 0; x < mask.getWidth(); x++) {
                for (int y = 0; y < mask.getHeight(); y++) {
                    if (mask.get(x, y) < 127) {
                        proc.drawDot(x + up - 1, y + left - 1);// -1 is BORDER
                    }

                }
            }

            image.repaintWindow();
        }

    }

    private Map<FilterProps, Integer> getSimilarProperties(Map<FilterProps, Integer> filterProperties, int procent) {

        Map<FilterProps, Integer> props = new HashMap<>();

        // DOWN
        props.put(FilterProps.AVG_INT_MIN, roundDown(lastFoundedProps.get(ComponentProperty.AVG_INTENSITY), procent));
        props.put(FilterProps.AVG_INT_MAX, roundUp(lastFoundedProps.get(ComponentProperty.AVG_INTENSITY), procent));

        props.put(FilterProps.INTENSITY_MIN, roundDown(lastFoundedProps.get(ComponentProperty.INTENSITY), procent));
        props.put(FilterProps.INTENSITY_MAX, roundUp(lastFoundedProps.get(ComponentProperty.INTENSITY), procent));

        props.put(FilterProps.ROUND_MIN, roundDown(lastFoundedProps.get(ComponentProperty.ROUND), procent));
        props.put(FilterProps.ROUND_MAX, roundUp(lastFoundedProps.get(ComponentProperty.ROUND), procent));

        props.put(FilterProps.ELONGATION_MIN, roundDown(lastFoundedProps.get(ComponentProperty.ELONGATION), procent));
        props.put(FilterProps.ELONGATION_MAX, roundUp(lastFoundedProps.get(ComponentProperty.ELONGATION), procent));

        props.put(FilterProps.SIZE_MIN, roundDown(lastFoundedProps.get(ComponentProperty.SIZE), procent));
        props.put(FilterProps.SIZE_MAX, roundUp(lastFoundedProps.get(ComponentProperty.SIZE), procent));
        return props;
    }

    private int roundDown(Integer val, int procent) {

        int nextVal = val * (100 - procent) / 100;
        return nextVal != val ? nextVal : val - 1;

    }

    private int roundUp(Integer val, int procent) {

        int nextVal = val * (100 + procent) / 100;
        return nextVal != val ? nextVal : val + 1;

    }

    private Map<ComponentProperty, Boolean> getSimilarFilters(Map<ComponentProperty, Boolean> selectedFilters) {
       
        Map<ComponentProperty, Boolean> filters = new HashMap<>();
          
        filters.put(ComponentProperty.SIZE, true);
        filters.put(ComponentProperty.ROUND, true);
        filters.put(ComponentProperty.AVG_INTENSITY, true);
        filters.put(ComponentProperty.ELONGATION, true);
  
        return filters;
    }

    public void filterCCTImage(CCT cct, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne, boolean similar) {

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
            if (node == null)
                return;
            proc.setColor(randomColor.getRandColor());
            for (Set<Point> subnode : node.getAllPoints()) {
                for (Point point : subnode) {
                    proc.drawDot(point.y, point.x);
                }
            }
            image.repaintWindow();
        }
    }

    public void reprintOrig() {
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
