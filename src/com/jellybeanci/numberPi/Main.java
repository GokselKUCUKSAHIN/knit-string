package com.jellybeanci.numberPi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("MainUI.fxml"));
        root.setOnKeyPressed(e->{
            switch (e.getCode())
            {
                case F1:
                {
                    Controller.update.play();
                    break;
                }
                case F2:
                {
                    Controller.clearScreen();
                    break;
                }
            }
        });
        stage.setTitle("JellyBeanci");
        stage.setResizable(false);
        stage.setScene(new Scene(root, 700, 700));
        stage.show();
        root.requestFocus();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
