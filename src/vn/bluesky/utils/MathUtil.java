package vn.bluesky.utils;

import java.util.Random;

/**
 * Created by asus on 11/22/2016.
 */

public class MathUtil {


    public static double randomDouble(double left, double right)
    {
        return left + (right-left)*(new Random().nextDouble());

    }
}
