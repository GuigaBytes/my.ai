package com.gltech.myai;

import com.gltech.myai.controllers.ChatController;
import com.gltech.myai.service.MessageService;
import com.gltech.myai.service.OpenAiChatService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder()
                .sources(Main.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        MessageService messageService = springContext.getBean(MessageService.class);
        OpenAiChatService openAiChatService = springContext.getBean(OpenAiChatService.class);
        ChatController chatController = new ChatController(primaryStage, springContext, messageService, openAiChatService);
        chatController.initialize();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
