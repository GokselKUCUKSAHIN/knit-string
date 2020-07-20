package com.jellybeanci.knit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class KnitArt
{

    public static ArrayList<Strand> deathStrand = new ArrayList<>();
    public static BooleanProperty isRealyDone = new SimpleBooleanProperty(false);
    public static Color forecolor = Color.color(0, 0, 0, 0.45);
    private static GraphicsContext graphicsContext;

    private static Image image;
    private static double[] imageData;
    private static Point2D[] pinList;
    public static ArrayList<Strand> lineList = new ArrayList<>();

    //public static final int LINE_LIMIT = 1500;
    public static final int LINE_LIMIT = 2300; //3000 is little too much
    private static int lineCount = 0;

    public KnitArt(GraphicsContext gc, int width, int height, int pinCount, Image img)
    {
        //
        graphicsContext = gc;
        image = img;
        this.pinList = generatePinList(pinCount, width, height);
        this.imageData = getImageArray(image);
        drawBorder();
        Runnable r = () -> {
            draw(graphicsContext, imageData, pinList, 0);
        };
        new Thread(r).start();
    }

    public static void drawBorder()
    {
        for (Point2D point : pinList)
        {
            graphicsContext.setStroke(Color.BLACK);
            graphicsContext.strokeOval(point.getX() - 0.5, point.getY() - 0.5, 2, 2);
        }
    }

    public static void draw(GraphicsContext gc, double[] imageData, Point2D[] pinList, int startPinIndex)
    {
        int endPinIndex = 0;
        double highestScore = 0;
        for (int i = 0; i < pinList.length; i++)
        {
            Point2D pin = pinList[i];
            if (isLineDrawn(lineList, startPinIndex, i) ||
                    isPinTooClose(pinList, startPinIndex, i) || startPinIndex == i)
            {
                continue; // was return but i think it's break. no it's actual continue
            }
            double score = getLineScore(imageData, pinList[startPinIndex], pin);
            if (score > highestScore)
            {
                endPinIndex = i;
                highestScore = score;
            }
        }
        lineCount++;
        if (lineCount <= LINE_LIMIT)
        {
            lineList.add(new Strand(startPinIndex, endPinIndex));
            //drawLine(graphicsContext, pinList[startPinIndex], pinList[endPinIndex], forecolor);
            addToArray(new Strand(startPinIndex, endPinIndex));
            reduceImageData(imageData, pinList[startPinIndex], pinList[endPinIndex]);
            draw(gc, imageData, pinList, endPinIndex);
        } else
        {
            isRealyDone.setValue(true);
        }
    }

    public static void addToArray(Strand strand)
    {
        deathStrand.add(strand);
    }

    public static Point2D[] generatePinList(int length, double width, double height)
    {
        Point2D[] array = new Point2D[length];
        Point2D center = new Point2D(width / 2, height / 2);
        final double RADIUS = width / 2;
        final double ANGLE_UNIT = (Math.PI * 2) / length;
        //
        for (int i = 0; i < length; i++)
        {
            double angle = ANGLE_UNIT * i - Math.PI / 2; //180 degree counter clock wise shift
            double x = Math.round(center.getX() + RADIUS * Math.cos(angle));
            double y = Math.round(center.getY() + RADIUS * Math.sin(angle));
            if (x == width)
            {
                array[i] = new Point2D(x - 1, y);
            }
            if (y == height)
            {
                array[i] = new Point2D(x, y - 1);
            }
            array[i] = new Point2D(x, y);
        }
        //
        return array;
    }

    public static boolean isDotOnLine(Point2D dot, Point2D start, Point2D end)
    {
        if (end.getX() - start.getX() == 0)
        {
            return (dot.getX() == end.getX());
        }
        //
        final double slope = (end.getY() - start.getY()) / (end.getX() - start.getX());
        final double intercept = start.getY() - slope * start.getX();

        final double blockTopY = dot.getY() + 0.5;
        final double blockBottomY = dot.getY() - 0.5;
        final double blockLeftY = slope * (dot.getX() - 0.5) + intercept;
        final double blockRightY = slope * (dot.getX() + 0.5) + intercept;

        if (Math.abs(slope) <= 1)
        {
            return ((blockLeftY >= blockBottomY && blockLeftY <= blockTopY) ||
                    (blockRightY >= blockBottomY && blockRightY <= blockTopY));
        } else
        {
            if (slope > 0)
            {
                return !(blockLeftY > blockTopY || blockRightY < blockBottomY);

            } else
            {
                return !(blockRightY > blockBottomY || blockLeftY < blockBottomY);
            }
        }
    }

    public static ArrayList<Point2D> getPointListOnLine(Point2D start, Point2D end)
    {
        ArrayList<Point2D> pointList = new ArrayList<>();
        int movementX = end.getX() > start.getX() ? 1 : -1;
        int movementY = end.getY() > start.getY() ? 1 : -1;

        double currenX = start.getX();
        double currenY = start.getY();

        int loopCount = 0;
        while ((currenX != end.getX() || currenY != end.getY()) && loopCount <= 1000)
        {
            pointList.add(new Point2D(currenX, currenY));
            if (isDotOnLine(new Point2D(currenX + movementX, currenY), start, end))
            {
                currenX += movementX;
            } else
            {
                currenY += movementY;
            }
            loopCount++;
        }
        pointList.add(end);
        return pointList;
    }

    public static void reduceImageData(double[] img, Point2D start, Point2D end)
    {
        ArrayList<Point2D> dotList = getPointListOnLine(start, end);
        for (Point2D dot : dotList)
        {
            try
            {
                int startIndex = (int) (dot.getY() * image.getWidth() + dot.getX());
                img[startIndex] += 0.196; // 50 / 255
                if (img[startIndex] > 1) // 255
                {
                    img[startIndex] = 1; // 255
                }
            }
            catch (Exception ex)
            {
                //System.out.println(dot.getX() + "; " + dot.getY());
            }
        }
    }

    public static double getLineScore(double[] img, Point2D start, Point2D end)
    {
        ArrayList<Point2D> dotList = getPointListOnLine(start, end);
        //
        ArrayList<Double> dotScoreList = new ArrayList<>();
        for (Point2D dot : dotList)
        {
            try
            {
                double colorR = img[(int) image.getWidth() * (int) dot.getY() + (int) dot.getX()];
                dotScoreList.add(1 - colorR);
            }
            catch (Exception ex)
            {
                // literally nothing.
                //System.out.println("img width: " + image.getWidth() + ": " + dot.getX() + "; " + dot.getY());
            }
        }

        double sum = 0;
        for (double dot : dotScoreList)
        {
            sum += dot;
        }
        return (sum / dotScoreList.size());
    }

    public static boolean isLineDrawn(ArrayList<Strand> lineList, int startPinIndex, int endPinIndex)
    {
        return Strand.find(lineList, startPinIndex, endPinIndex);
    }

    public static boolean isPinTooClose(Point2D[] pinList, int startPinIndex, int endPinIndex)
    {
        double pinDistance = Math.abs(endPinIndex - startPinIndex);
        pinDistance = pinDistance > pinList.length / 2 ? pinList.length - pinDistance : pinDistance;
        return pinDistance < 25;
    }

    public static void drawLine(GraphicsContext gc, Point2D start, Point2D end, Color color)
    {
        gc.setStroke(color);
        gc.setLineWidth(0.4);
        gc.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public static void drawLine(GraphicsContext gc, Strand strand, Color color)
    {
        gc.setStroke(color);
        gc.setLineWidth(0.4);
        double[] points = strand.getLineCoordinates(pinList);
        gc.strokeLine(points[0], points[1], points[2], points[3]);
    }

    public static String printArray(int[] arr)
    {
        String str = "[";
        for (int i = 0; i < arr.length - 1; i++)
        {
            str += String.format("%3d, ", arr[i]);
        }
        str += String.format("%3d]", arr[arr.length - 1]);
        return str;
    }

    public static double[] getImageArray(Image img)
    {
        // row, colum
        // y  , x
        // img[y,x]
        PixelReader pixelReader = img.getPixelReader();
        double[] imgArr = new double[(int) img.getHeight() * (int) img.getWidth()];
        int index = 0;
        for (int y = 0; y < img.getHeight(); y++)
        {
            for (int x = 0; x < img.getWidth(); x++)
            {
                imgArr[index++] = pixelReader.getColor(x, y).getRed();
            }
        }
        return imgArr;
    }

    public static Point2D[] getArray(ArrayList<Point2D> list)
    {
        Point2D[] array = new Point2D[list.size()];
        for (int i = 0; i < array.length; i++)
        {
            array[i] = list.get(i);
        }
        return array;
    }
}