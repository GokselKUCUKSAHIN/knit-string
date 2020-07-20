package com.jellybeanci.knit;

import java.util.Random;

public class Rand
{

    private static Random random = new Random(); //static random generator //one generator for everything!

    static int getInt(int start, int end)
    {
        return random.nextInt(end - start) + start;
    }

    static int getInt(int limit)
    {
        return random.nextInt(limit);
    }

    static double getDouble(double min, double max)
    {
        return Utils.map(Math.random(), 0, 1, min, max);
    }

    static double getDouble(double limit)
    {
        return Utils.map(Math.random(), 0, 1, 0, limit);
    }

    static float getFloat(float min, float max)
    {
        return (float) getDouble(min, max);
    }
}