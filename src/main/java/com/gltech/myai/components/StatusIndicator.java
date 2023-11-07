package com.gltech.myai.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

public class StatusIndicator extends HBox {

    private Circle statusCircle;
    @Getter
    private ProgressIndicator spinner;

    public StatusIndicator() {
        initializeComponents();
    }

    private void initializeComponents() {
        Label statusLabel = new Label("Status: ");
        statusCircle = new Circle(5, Color.GREEN);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        spinner = new ProgressIndicator();
        spinner.setPrefSize(15, 15);
        spinner.setVisible(false);

        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(5);
        this.setPadding(new Insets(5, 10, 5, 0));
        this.getChildren().addAll(statusLabel, statusCircle, spacer, spinner);
    }

    public void setStatusColor(Color color) {
        statusCircle.setFill(color);
    }

}
