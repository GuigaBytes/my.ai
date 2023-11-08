package com.gltech.myai.model;

import lombok.Getter;

public enum ModelType {
    DAVINCI("text-davinci-003", "DaVinci"),
    GPT_TURBO("gpt-3.5-turbo", "GPT-3.5 Turbo"),
    CURIE("text-curie-001", "Curie"),
    BABBAGE("text-babbage-001", "Babbage"),
    ADA("text-ada-001", "Ada");

    @Getter
    private final String value;
    @Getter
    private final String label;

    ModelType(String value, String label) {

        this.value = value;
        this.label = label;
    }

    @Override
    public String toString() {
        return value;
    }
}
