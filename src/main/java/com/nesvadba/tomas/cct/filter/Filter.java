package com.nesvadba.tomas.cct.filter;

import com.nesvadba.tomas.cct.enums.ComponentProperty;
import com.nesvadba.tomas.cct.enums.FilterProps;

public class Filter {

    protected static FilterProps getProperty(ComponentProperty property, boolean isMinSelected) {
        switch (property) {
            case SIZE :
                return (isMinSelected) ? FilterProps.SIZE_MIN : FilterProps.SIZE_MAX;
            case G_HEIGHT :
                return (isMinSelected) ? FilterProps.HEIGHT_MIN : FilterProps.HEIGHT_MAX;
            case INTENSITY :
                return (isMinSelected) ? FilterProps.INTENSITY_MIN : FilterProps.INTENSITY_MAX;
                
           //BOUNDING BOX
            case LEFT :
                return (isMinSelected) ? FilterProps.LEFT : FilterProps.MAX;
            case RIGHT :
                return (isMinSelected) ? FilterProps.MIN : FilterProps.RIGHT;
            case UP :
                return (isMinSelected) ? FilterProps.UP : FilterProps.MAX;
            case DOWN :
                return (isMinSelected) ? FilterProps.MIN : FilterProps.BOTTOM;
            default :
                break;
        }
        return null;
    }
}
