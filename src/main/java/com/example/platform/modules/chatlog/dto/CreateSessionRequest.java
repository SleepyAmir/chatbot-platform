package com.example.platform.modules.chatlog.dto;

import lombok.Data;

/**
 * userId اختیاری است: اگر خالی/null ارسال شود، ChatSessionService.createSession
 * به‌صورت متمرکز به یک کاربر مهمان (guest-user) فالبک می‌زند، نه اینکه درخواست
 * را رد کند — هم‌خوان با رفتار چت بدون session که از قبل همین قانون را داشت.
 */
@Data
public class CreateSessionRequest {

    private String userId;
}