package com.nesvadba.tomas.cct.domain;

import com.nesvadba.tomas.cct.enums.PointStatus;

public class Point {

    public int x;
    public int y;
    public PointStatus status;
    public int value;

    @Override
    public String toString() {
	return "Point [x=" + x + ", y=" + y + ", value=" + value + "]";
    }

}
