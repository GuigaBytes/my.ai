package com.gltech.myai.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Service
public class OpenAiChatService {

    private OpenAiService openAiService;

    @Value("${openai.api.key}")
    private String apiKey;

    private String internalContext;

    @PostConstruct
    public void init() {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.internalContext = "";
    }

    public String sendChatMessage(String prompt) {
        internalContext += prompt + "\n";
        System.out.println("Context: " + internalContext);
        CompletionRequest completionRequest = CompletionRequest.builder()
            .model("text-davinci-003")
            .prompt(internalContext)
            .maxTokens(150)
            .build();

        String responseText = openAiService.createCompletion(completionRequest).getChoices().get(0).getText();
        System.out.println("OpenAI response: " + responseText);
        return responseText;
    }

    public void shutdownOpenAiService() {
        openAiService.shutdownExecutor();
    }

    public void resetInternalContext() {
        internalContext = "";
    }
}
