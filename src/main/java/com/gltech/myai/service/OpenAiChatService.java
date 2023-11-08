package com.gltech.myai.service;

import com.gltech.myai.model.ModelType;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;

@Service
public class OpenAiChatService {

    private OpenAiService openAiService;

    @Value("${openai.api.key}")
    private String apiKey;

    private String internalContext;

    @Getter
    private String model = String.valueOf(ModelType.GPT_TURBO);

    @PostConstruct
    public void init() {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.internalContext = "";
    }

    public OpenAiChatService setModel(ModelType modelType) {
        this.model = String.valueOf(modelType);
        return this;
    }

    public String sendChatMessage(String prompt) {
        updateInternalContext(prompt);
        System.out.println("Selected model: " + model);
        System.out.println("Context: " + internalContext);

        return model.equals(ModelType.GPT_TURBO.toString()) ?
                handleChatCompletion() : handleRegularCompletion();
    }

    private void updateInternalContext(String message) {
        internalContext += message + "\n";
    }

    private String handleChatCompletion() {
        ChatCompletionRequest chatRequest = createChatCompletionRequest();
        System.out.println("OpenAI chat request: " + chatRequest);
        ChatCompletionResult chatResult = openAiService.createChatCompletion(chatRequest);
        System.out.println("OpenAI chat response: " + chatResult);
        return extractResponseFromChatResult(chatResult);
    }

    private ChatCompletionRequest createChatCompletionRequest() {
        return ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(new ChatMessage(ChatMessageRole.USER.toString(), internalContext)))
                .maxTokens(150)
                .build();
    }

    private String extractResponseFromChatResult(ChatCompletionResult chatResult) {
        String responseText = chatResult.getChoices().get(0).getMessage().getContent();
        System.out.println("OpenAI chat response text: " + chatResult.getChoices().get(0));
        return responseText;
    }

    private String handleRegularCompletion() {
        CompletionRequest completionRequest = createCompletionRequest();
        CompletionResult completionResult = openAiService.createCompletion(completionRequest);
        System.out.println("OpenAI completion response: " + completionResult);
        return extractResponseFromCompletionResult(completionResult);
    }

    private CompletionRequest createCompletionRequest() {
        return CompletionRequest.builder()
                .model(model)
                .prompt(internalContext)
                .maxTokens(150)
                .build();
    }

    private String extractResponseFromCompletionResult(CompletionResult completionResult) {
        String responseText = completionResult.getChoices().get(0).getText();
        System.out.println("OpenAI completion response text: " + responseText);
        return responseText;
    }

    public void shutdownOpenAiService() {
        openAiService.shutdownExecutor();
    }

    public void resetInternalContext() {
        internalContext = "";
    }
}
