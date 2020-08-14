package com.jellybeanci.knit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
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
    //Pane rootPane;


    @FXML
    protected void aboutClick()
    {
        showMessage("About", "This app created by GokselKUCUKSAHIN", Alert.AlertType.INFORMATION);
    }

    @FXML
    protected void saveClick()
    {
        if (count >= KnitArt.LINE_LIMIT)
        {
            saveFile(this.canvas);
        }
        else
        {
            System.out.println("mada mada");
        }
    }


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
        update.setRate(2.5);
        update.setAutoReverse(false);

        // TODO UNCOMMENT LATER

        now();
        KnitArt.isRealyDone.addListener((observable, oldValue, newValue) -> {
            System.out.println("READY!!!");
            System.out.println("RecudeImageDataErr: " + KnitArt.errCountRecude + "; GetLineScoreErr: " + KnitArt.errCountScore);
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

    private void frame()
    {
        if (count < KnitArt.deathStrand.size())
        {
            KnitArt.drawLine(gc, KnitArt.deathStrand.get(count++), KnitArt.forecolor);
        } else if (count >= KnitArt.LINE_LIMIT)
        {
            update.pause();
            //System.out.println("SAVE");
            //saveFile(this.canvas);
            Platform.runLater(() -> {
                // Update UI here.
                saveAsBigPicture();
            });
        }
    }

    static WritableImage wim = new WritableImage(600, 600);
    static WritableImage highRes = new WritableImage(6000, 6000);

    protected static void saveFile(Canvas canvas)
    {
        File file = new File("CanvasImage.png");
        canvas.snapshot(null, wim);
        try
        {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
        }
        catch (Exception s)
        {
        }
    }

    public static void saveAsBigPicture()
    {
        Canvas nCanvas = new Canvas(6000, 6000);
        GraphicsContext highResGC = nCanvas.getGraphicsContext2D();
        highResGC.setFill(Color.WHITE);
        highResGC.fillRect(0, 0, 6000, 6000); // background!
        highResGC.setLineWidth(2);
        highResGC.setStroke(Color.color(0, 0, 0, 0.6));
        Point2D[] highPinList = KnitArt.generatePinList(400, 6000, 6000);
        for (int i = 0; i < KnitArt.lineList.size(); i++)
        {
            Strand _strand = KnitArt.lineList.get(i);
            Point2D start = highPinList[_strand.startPinIndex];
            Point2D end = highPinList[_strand.endPinIndex];
            highResGC.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
        }
        System.out.println("SAVING... Please Wait");
        File file = new File("HIGH_RES.png");
        nCanvas.snapshot(null, highRes);
        Runnable r = () -> {
            try
            {
                ImageIO.write(SwingFXUtils.fromFXImage(highRes, null), "png", file);
            }
            catch (Exception s)
            {
            }
            finally
            {
                System.out.println("SAVED!");
            }
        };
        new Thread(r).start();
    }


    private static void timeStamp()
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = ts;
        System.out.println(date);
    }

    private static void showMessage(String title, String message, Alert.AlertType alertType)
    {
        Platform.runLater(() -> {
            // Update UI here.
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
