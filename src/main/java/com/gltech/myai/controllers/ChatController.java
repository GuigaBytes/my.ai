package com.gltech.myai.controllers;

import com.gltech.myai.components.MessageArea;
import com.gltech.myai.components.StatusIndicator;
import com.gltech.myai.components.InputField;
import com.gltech.myai.model.Message;
import com.gltech.myai.model.MessageType;
import com.gltech.myai.model.ModelType;
import com.gltech.myai.service.MessageService;
import com.gltech.myai.service.OpenAiChatService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Objects;

public class ChatController {

    private final Stage stage;
    private final StatusIndicator statusIndicator;
    private final MessageArea messageArea;
    private final InputField inputField;
    private final MessageService messageService;
    private final OpenAiChatService openAiChatService;

    public ChatController(Stage stage, ConfigurableApplicationContext springContext, MessageService messageService, OpenAiChatService openAiChatService) {
        this.stage = stage;
        this.statusIndicator = new StatusIndicator();
        this.messageArea = new MessageArea();
        this.inputField = new InputField();
        this.messageService = messageService;
        this.openAiChatService = openAiChatService;
    }

    public void initialize() {
        MenuBar menuBar = createMenuBar();
        VBox root = new VBox(5);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(menuBar, messageArea, inputField, statusIndicator);
        VBox.setVgrow(messageArea, Priority.ALWAYS);

        Image applicationIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/ico.png")));
        stage.getIcons().add(applicationIcon);

        Scene scene = new Scene(root, 360, 640);
        stage.setTitle("My.ai");
        stage.setScene(scene);
        stage.setResizable(false);

        loadMessagesFromDatabase();

        hookEvents();
        stage.show();
        inputField.getTextField().requestFocus();
    }

    private void sendMessage() {
        showSpinner();
        String messageContent = inputField.getTextField().getText().trim();
        inputField.getTextField().clear();
        if (!messageContent.isEmpty()) {
            messageContent = messageContent.trim();

            String finalMessageContent = messageContent;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Message savedMessage = messageService.saveMessage(finalMessageContent, MessageType.USER);
                    Platform.runLater(() -> messageArea.appendMessage(savedMessage));

                    String response = openAiChatService.sendChatMessage(finalMessageContent);
                    Platform.runLater(() -> {
                        Message systemResponse = messageService.saveMessage(response.trim(), MessageType.SYSTEM);
                        messageArea.appendMessage(systemResponse);
                        hideSpinner();
                    });

                    return null;
                }
            };

            // Em caso de falha, esconder o spinner
            task.setOnFailed(e -> hideSpinner());

            new Thread(task).start();
        } else {
            hideSpinner();
        }
    }

    private void hookEvents() {
        // Evento do botão de enviar
        inputField.getSendButton().setOnAction(event -> sendMessage());

        // Evento do campo de texto para o Enter
        inputField.getTextField().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    private void loadMessagesFromDatabase() {
        List<Message> messages = messageService.findAllMessages();
        for (Message message : messages) {
            messageArea.appendMessage(message);
        }
    }

    private MenuBar createMenuBar() {
    MenuBar menuBar = new MenuBar();

    // Menu "File" já existente
    Menu menuFile = new Menu("File");
    MenuItem itemAutoDestroy = new MenuItem("Apagar memória");
    itemAutoDestroy.setOnAction(event -> autoDestroyMessages());
    menuFile.getItems().add(itemAutoDestroy);

    // Novo menu "Modelos"
    Menu menuModels = new Menu("Modelos");

    // Grupo de toggle para os RadioMenuItems
    ToggleGroup modelsGroup = new ToggleGroup();

    // Criar os itens do menu com um método que trata a seleção do modelo
    RadioMenuItem itemDavinci = createModelMenuItem(ModelType.DAVINCI, modelsGroup);
    RadioMenuItem itemGPT35Turbo = createModelMenuItem(ModelType.GPT_TURBO, modelsGroup);
    itemGPT35Turbo.setSelected(true); // Este é o default
    RadioMenuItem itemCurie = createModelMenuItem(ModelType.CURIE, modelsGroup);
    RadioMenuItem itemBabbage = createModelMenuItem(ModelType.BABBAGE, modelsGroup);
    RadioMenuItem itemAda = createModelMenuItem(ModelType.ADA, modelsGroup);

    // Adicionando todos os RadioMenuItems ao menu "Modelos"
    menuModels.getItems().addAll(itemDavinci, itemGPT35Turbo, itemCurie, itemBabbage, itemAda);

    // Adicionando o menu "Modelos" à barra de menu
    menuBar.getMenus().add(menuModels);

    // Adiciona o menu "File" à barra de menu
    menuBar.getMenus().add(menuFile);

    return menuBar;
}

    private RadioMenuItem createModelMenuItem(ModelType model, ToggleGroup group) {
        RadioMenuItem menuItem = new RadioMenuItem(model.getLabel());
        menuItem.setToggleGroup(group);
        menuItem.setOnAction(event -> handleModelSelection(model));
        return menuItem;
    }

    private void handleModelSelection(ModelType model) {
        openAiChatService.setModel(model);
    }

    private void autoDestroyMessages() {
        showSpinner();
        messageService.deleteAllMessages();
        messageArea.clearMessages();
        openAiChatService.resetInternalContext();
        hideSpinner();
    }

    private void showSpinner() {
        statusIndicator.getSpinner().setVisible(true);
    }

    private void hideSpinner() {
        statusIndicator.getSpinner().setVisible(false);
    }
}
