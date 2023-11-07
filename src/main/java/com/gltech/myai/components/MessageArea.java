package com.gltech.myai.components;

import com.gltech.myai.model.Message;
import com.gltech.myai.model.MessageType;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MessageArea extends VBox {

    public void appendMessage(Message message) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(message.getContent());
        messageBox.getChildren().add(messageLabel);

        String style = message.getType() == MessageType.USER ? "-fx-border-color: blue;" : "-fx-border-color: red;";
        messageBox.setStyle(style + "-fx-padding: 5; -fx-border-width: 2;");
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setWrapText(true);

        this.getChildren().add(messageBox);
    }

    public void clearMessages() {
        this.getChildren().clear();
    }
}
