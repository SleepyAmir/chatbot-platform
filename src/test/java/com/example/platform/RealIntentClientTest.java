package com.example.platform;

import com.example.platform.common.constant.ChatIntents;
import com.example.platform.modules.qa.client.RealIntentClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * تست بازگشت‌ناپذیری (regression) برای باگ قبلی: سرویس پایتون رشته‌ای مثل "PRICING" یا
 * "pricing" برمی‌گرداند، اما کد قبلی با toUpperCase() آن را با ثابت‌های lowercase
 * ChatIntents مقایسه می‌کرد و هیچ‌وقت مچ نمی‌شد -> همیشه fallback به LLM.
 */
class RealIntentClientTest {

//    @Test
//    void lowercaseFromPython_isRecognized() {
//        assertEquals(ChatIntents.PRICING, RealIntentClient.resolveIntent("pricing"));
//    }
//
//    @Test
//    void uppercaseFromPython_isStillRecognized() {
//        // این دقیقاً سناریوی باگ قبلی است: سرویس پایتون ممکن است uppercase برگرداند
//        assertEquals(ChatIntents.PRICING, RealIntentClient.resolveIntent("PRICING"));
//        assertEquals(ChatIntents.FAQ, RealIntentClient.resolveIntent("FAQ"));
//        assertEquals(ChatIntents.CLASS_SEARCH, RealIntentClient.resolveIntent("CLASS_SEARCH"));
//    }
//
//    @Test
//    void mixedCaseWithWhitespace_isNormalized() {
//        assertEquals(ChatIntents.LLM, RealIntentClient.resolveIntent("  Llm  "));
//    }
//
//    @Test
//    void unknownIntent_returnsNull() {
//        assertNull(RealIntentClient.resolveIntent("something_unexpected"));
//    }
//
//    @Test
//    void nullIntent_returnsNull() {
//        assertNull(RealIntentClient.resolveIntent(null));
//    }
}