package com.nesvadba.tomas.cct;

import java.awt.Color;
import java.util.Random;

public class RandomColor {
    private final Random randomGenerator = new Random();
    
    public Color getRandColor (){
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);

        return new Color(red, green, blue);
    }
}
