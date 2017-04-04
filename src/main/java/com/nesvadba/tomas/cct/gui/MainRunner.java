package com.nesvadba.tomas.cct.gui;

import ij.IJ;
import ij.ImagePlus;

public class MainRunner {
    
    
    public static void main(String [] args){
 
        
//       ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\_nak.tif");
       ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\medium.tif");
//       ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\small.tif");
//        ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\pokus2.tif");
//        ImagePlus img = IJ.openImage("C:\\Users\\nipaba\\Documents\\img\\NAK\\big.tif");
       img.show();
       
       CCT_PluginFrame frame = new CCT_PluginFrame();
       frame.show();
       frame.run("run");
    }
}
