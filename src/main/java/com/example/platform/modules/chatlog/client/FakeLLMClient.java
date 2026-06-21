package com.example.platform.modules.chatlog.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"local"})
public class FakeLLMClient implements LLMClient {
    @Override
    public String ask(String question) {
        return "این یک پاسخ هوشمند فرضی برای سوال شماست: " + question;
    }
}


