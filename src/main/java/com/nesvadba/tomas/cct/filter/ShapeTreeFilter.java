package com.nesvadba.tomas.cct.filter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;

public class ShapeTreeFilter extends Filter {

    public static Set<ShapeTree> filterByProperties(ShapeTree shapeTree, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {

        Set<ShapeTree> result = new HashSet<>();
        Queue<ShapeTree> que = new LinkedList<>();
        que.add(shapeTree);

        boolean minSelected = true;
        
        ShapeTree onlyNode = null;

        while (!que.isEmpty()) {
            ShapeTree node = que.poll();
            boolean tempInRANGE = true;
            for (ComponentProperty property : selectedFilters.keySet()) {

                if (selectedFilters.get(property)) {
                    int propertyVal = node.getProperties().get(property);

                    int min = filterProperties.get(getProperty(property, minSelected));
                    int max = filterProperties.get(getProperty(property, !minSelected));

                    tempInRANGE = tempInRANGE & (propertyVal < max && propertyVal > min);
                }
            }
            if (tempInRANGE) {
                result.add(node);
                if (onlyNode == null) {
                    onlyNode = node;
                } else if (onlyNode.getProperties().get(ComponentProperty.SIZE) < node.getProperties().get(ComponentProperty.SIZE)) {
                    onlyNode = node;
                }
            } else {
                que.addAll(node.getNodes());
            }
        }
        
        if (keepOnlyOne) {
            result = new HashSet<>();
            result.add(onlyNode);
        }
        return result;
    }


}
