import { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getChatSessionHistory, sendChatMessage, sendChatWithFile } from '../../../api/chat.api';
import type { ChatUiMessage } from '../../../types/chat';

const SESSION_STORAGE_KEY = 'mft-chat-session-id';

function createMessageId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}

function mapHistoryToUi(messages: { role: 'user' | 'assistant'; content: string; timestamp?: string }[]): ChatUiMessage[] {
  return messages.map((message, index) => ({
    id: message.timestamp ?? `history-${index}`,
    role: message.role,
    content: message.content,
    timestamp: message.timestamp ? new Date(message.timestamp) : new Date(),
  }));
}

export function useChat() {
  const { t } = useTranslation();
  const [messages, setMessages] = useState<ChatUiMessage[]>([]);
  const [sessionId, setSessionId] = useState<string | null>(() => sessionStorage.getItem(SESSION_STORAGE_KEY));
  const [loading, setLoading] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const bottomRef = useRef<HTMLDivElement>(null);

  const persistSessionId = useCallback((id: string) => {
    sessionStorage.setItem(SESSION_STORAGE_KEY, id);
    setSessionId(id);
  }, []);

  const loadHistory = useCallback(async (id: string) => {
    setHistoryLoading(true);
    try {
      const history = await getChatSessionHistory(id);
      setMessages(mapHistoryToUi(history));
      setError(null);
    } catch {
      setMessages([]);
    } finally {
      setHistoryLoading(false);
    }
  }, []);

  useEffect(() => {
    if (sessionId) {
      void loadHistory(sessionId);
    }
  }, [sessionId, loadHistory]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const startNewSession = useCallback(() => {
    sessionStorage.removeItem(SESSION_STORAGE_KEY);
    setSessionId(null);
    setMessages([]);
    setError(null);
  }, []);

  const sendMessage = useCallback(
    async (question: string, file?: File | null) => {
      const trimmed = question.trim();
      if (!trimmed || loading) {
        return;
      }

      const userMessage: ChatUiMessage = {
        id: createMessageId(),
        role: 'user',
        content: file ? `${trimmed}\n📎 ${file.name}` : trimmed,
        timestamp: new Date(),
      };

      setMessages((current) => [...current, userMessage]);
      setLoading(true);
      setError(null);

      try {
        const response = file
          ? await sendChatWithFile({ question: trimmed, sessionId: sessionId ?? undefined, file })
          : await sendChatMessage({ question: trimmed, sessionId: sessionId ?? undefined });

        persistSessionId(response.sessionId);

        if (!response.success) {
          const errorMessage: ChatUiMessage = {
            id: createMessageId(),
            role: 'assistant',
            content: response.error || t('chat.noResponse'),
            timestamp: new Date(),
            error: true,
          };
          setMessages((current) => [...current, errorMessage]);
          return;
        }

        const assistantMessage: ChatUiMessage = {
          id: createMessageId(),
          role: 'assistant',
          content: response.answer || t('chat.noResponse'),
          timestamp: new Date(),
        };
        setMessages((current) => [...current, assistantMessage]);
      } catch (err) {
        const message = err instanceof Error ? err.message : t('chat.sendError');
        setError(message);
        setMessages((current) => [
          ...current,
          {
            id: createMessageId(),
            role: 'assistant',
            content: message,
            timestamp: new Date(),
            error: true,
          },
        ]);
      } finally {
        setLoading(false);
      }
    },
    [loading, persistSessionId, sessionId, t],
  );

  return {
    messages,
    sessionId,
    loading,
    historyLoading,
    error,
    bottomRef,
    sendMessage,
    startNewSession,
  };
}
