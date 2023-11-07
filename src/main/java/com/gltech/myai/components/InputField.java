package com.gltech.myai.components;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class InputField extends HBox {

    private TextField textField;
    private Button sendButton;

    public InputField() {
        textField = new TextField();
        sendButton = new Button("Enviar");
        buildLayout();
    }

    private void buildLayout() {
        HBox.setHgrow(textField, Priority.ALWAYS);
        this.getChildren().addAll(textField, sendButton);
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getSendButton() {
        return sendButton;
    }
}
