package com.nesvadba.tomas.cct;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nesvadba.tomas.cct.domain.CCT;
import com.nesvadba.tomas.cct.domain.Point;
import com.nesvadba.tomas.cct.enums.ComponentProperty;

public class CCTEvaluator {

    public static void evaluateNewPoint(CCT cct, Point p) {
        Set<Point> points = cct.getPoints();
        Map<ComponentProperty, Integer> properties = cct.getProperties();
        List<CCT> nodes = cct.getNodes();

        // init
        int temp, left, right, up, down, height;

        int centerX = 0;
        int centerY = 0;
        int size = 0;
        height = 1;

        if (points.size() == 1) {
            left = p.x;
            right = p.x;
            up = p.y;
            down = p.y;

        } else {
            left = properties.get(ComponentProperty.LEFT);
            right = properties.get(ComponentProperty.RIGHT);
            up = properties.get(ComponentProperty.UP);
            down = properties.get(ComponentProperty.DOWN);
        }

        // process SUB
        for (CCT node : nodes) {

            // Size
            size += node.getProperties().get(ComponentProperty.SIZE);

            // BB
            temp = node.getProperties().get(ComponentProperty.LEFT);
            if (temp < left) {
                left = temp;
            }

            temp = node.getProperties().get(ComponentProperty.RIGHT);
            if (temp > right) {
                right = temp;
            }

            temp = node.getProperties().get(ComponentProperty.UP);
            if (temp < up) {
                up = temp;
            }

            temp = node.getProperties().get(ComponentProperty.DOWN);
            if (temp > down) {
                down = temp;
            }
            // Height
            height = Integer.max(height, node.getProperties().get(ComponentProperty.G_HEIGHT) + 1);

            // CENTER
            centerX += node.getProperties().get(ComponentProperty.CENTERX);
            centerY += node.getProperties().get(ComponentProperty.CENTERY);

        }

        // BB Calc
        if (p.x < left) {
            left = p.x;
        }
        if (p.x > right) {
            right = p.x;
        }
        if (p.y < up) {
            up = p.y;
        }
        if (p.y > down) {
            down = p.y;
        }

        centerX += p.x;
        centerY += p.y;

        properties.put(ComponentProperty.LEFT, left);
        properties.put(ComponentProperty.RIGHT, right);
        properties.put(ComponentProperty.UP, up);
        properties.put(ComponentProperty.DOWN, down);

        // HEIGHT
        properties.put(ComponentProperty.G_HEIGHT, height);

        // SIZE
        properties.put(ComponentProperty.SIZE, points.size() + size);

        // INTENSITY
        properties.put(ComponentProperty.INTENSITY, p.value);

        // BOunding box
        properties.put(ComponentProperty.LEFT, left);
        properties.put(ComponentProperty.RIGHT, right);
        properties.put(ComponentProperty.UP, up);
        properties.put(ComponentProperty.DOWN, down);

        // Center X,Y
        properties.put(ComponentProperty.CENTERX, centerX);
        properties.put(ComponentProperty.CENTERY, centerY);
    }

}
