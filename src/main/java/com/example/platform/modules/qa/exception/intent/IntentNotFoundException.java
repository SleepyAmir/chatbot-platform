package com.example.platform.modules.qa.exception.intent;

public class IntentNotFoundException extends RuntimeException {
    public IntentNotFoundException(String message) {
        super(message);
    }

    public static IntentNotFoundException byId(Integer id) {
        throw new IntentNotFoundException("intent with id " + id + " not Found");
    }

    public static IntentNotFoundException byName(String name) {
        throw new IntentNotFoundException("intent with name " + name + " not Found");
    }
}
