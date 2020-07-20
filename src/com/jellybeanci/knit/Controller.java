package com.jellybeanci.knit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Controller
{

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    //
    public static Timeline update;
    private static Color backcolor = Color.rgb(51, 51, 51);
    private static Color forecolor;
    private static GraphicsContext gc;
    private static Point2D center;
    private static double hue = 0;
    private Point2D prev;

    Image img;

    @FXML
    Canvas canvas;

    @FXML
    BorderPane rootPane;

    private static Image loadImage(String name) throws IOException
    {
        return new Image(new FileInputStream("img/" + name));
    }

    @FXML
    private void initialize()
    {
        timeStamp();
        center = new Point2D(WIDTH / 2, HEIGHT / 2);
        gc = canvas.getGraphicsContext2D();
        //
        update = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            frame();
        }));
        update.setCycleCount(Timeline.INDEFINITE);
        update.setRate(2);
        update.setAutoReverse(false);
        //
        now();
        KnitArt.isRealyDone.addListener((observable, oldValue, newValue) -> {
            System.out.println("READY!!!");
            timeStamp();
        });
    }

    public static void now()
    {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 600, 600);
        //drawLine(new Point2D(0,0),new Point2D(600,600), Color.RED);
        try
        {
            KnitArt knitArt = new KnitArt(gc, 600, 600, 400, loadImage("me_close.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void clearScreen()
    {
        //gc.setFill(Color.RED);
        //gc.fillRect(0, 0, 600, 600);
        gc.clearRect(0, 0, 610, 610);
    }


    private static void drawLine(Point2D startPoint, Point2D endPoint, Color color)
    {
        gc.setStroke(color);
        gc.setLineWidth(0.6);
        gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
    }


    private static void drawDot(double x, double y, Color color)
    {
        gc.setStroke(color);
        gc.setLineWidth(1); //1
        //gc.strokeRect(x + 0.5, y + 0.5, 0.5, 0.5);
        gc.strokeOval(x + 0.5, y + 0.5, 0.3, 0.3);
    }

    private static int count = 0;

    private static void frame()
    {
        if (count < KnitArt.deathStrand.size())
        {
            KnitArt.drawLine(gc, KnitArt.deathStrand.get(count++), KnitArt.forecolor);
        }
    }

    private static void timeStamp()
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = ts;
        System.out.println(date);
    }
}
