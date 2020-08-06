package com.kitchas.kitchenassistant.assistant;

public class Assistant {
    private static final String vocePath = "../../../lib";
    private static Assistant kitchenAssistant = null;

    private Assistant() {
        voce.SpeechInterface.init(vocePath, false, true, "./grammar", "digits");
        startListening();
    }

    public static Assistant getInstance() {
        if (kitchenAssistant == null) {
            kitchenAssistant = new Assistant();
        }

        return kitchenAssistant;
    }

    public void start() {

    }

    private void startListening() {
        voce.SpeechInterface.setRecognizerEnabled(true);
    }

    private void stopListening() {
        voce.SpeechInterface.setRecognizerEnabled(false);
    }

    public String listen() {
        return voce.SpeechInterface.popRecognizedString();
    }
}