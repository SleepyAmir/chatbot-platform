package com.example.platform.modules.qa.exception.qa_pair;

public class QaPairNotFoundException extends RuntimeException {

    private QaPairNotFoundException(String message) {
        super(message);
    }

    public static QaPairNotFoundException byId(Integer id) {
        return new QaPairNotFoundException("QaPair not found with id: " + id);
    }
}