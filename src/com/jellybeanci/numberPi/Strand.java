package com.jellybeanci.numberPi;

import com.sun.istack.internal.Nullable;
import javafx.geometry.Point2D;

import java.util.ArrayList;

public class Strand
{

    public int startPinIndex;
    public int endPinIndex;

    public Strand(int startPinIndex, int endPinIndex)
    {
        this.startPinIndex = startPinIndex;
        this.endPinIndex = endPinIndex;
    }

    public double[] getLineCoordinates(Point2D[] pinList)
    {
        return new double[]{
                pinList[this.startPinIndex].getX(),
                pinList[this.startPinIndex].getY(),
                pinList[this.endPinIndex].getX(),
                pinList[this.endPinIndex].getY()
        };
    }

    public static boolean find(ArrayList<Strand> list, Strand line)
    {
        return find(list, line.startPinIndex, line.endPinIndex);
    }

    public static boolean find(ArrayList<Strand> list, int startPinIndex, int endPinIndex)
    {
        for (Strand strand : list)
        {
            if ((strand.startPinIndex == startPinIndex && strand.endPinIndex == endPinIndex) ||
                    (strand.startPinIndex == endPinIndex && strand.endPinIndex == startPinIndex))
            {
                return true;
            }
        }
        return false;
    }
}
