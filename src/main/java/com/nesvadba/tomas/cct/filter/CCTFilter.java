package com.nesvadba.tomas.cct.filter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.ShapeTree;
import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;

public class CCTFilter extends Filter {

    public static Set<CCT> filterByProperties(CCT cct, Map<FilterProps, Integer> filterProperties, Map<ComponentProperty, Boolean> selectedFilters, boolean keepOnlyOne) {

        Set<CCT> result = new HashSet<>();
        Queue<CCT> que = new LinkedList<>();
        que.add(cct);

        boolean minSelected = true;

        CCT onlyNode = null;

        while (!que.isEmpty()) {
            CCT node = que.poll();
            boolean tempInRANGE = true;
            for (ComponentProperty property : selectedFilters.keySet()) {
                if (selectedFilters.get(property)) {
                    
                    if (!isAviable(property,node.getProperties(),filterProperties)){
                        return new HashSet<>();
                    }
                    int propertyVal = node.getProperties().get(property).intValue();
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
            
            System.out.println("CCTFilter : filterByProperties := " + onlyNode.getProperties()); // TODO LOG REMOVE
        }
        return result;
    }

}
