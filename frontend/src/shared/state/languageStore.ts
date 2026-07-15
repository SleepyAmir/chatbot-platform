import { create } from 'zustand';
import i18n from '../../i18n';

export type Language = 'fa' | 'en';

type LanguageState = {
  language: Language;
  setLanguage: (language: Language) => void;
  toggleLanguage: () => void;
};

const storageKey = 'chatbot-platform-language';

function getInitialLanguage(): Language {
  const storedLanguage = window.localStorage.getItem(storageKey);
  return storedLanguage === 'en' ? 'en' : 'fa';
}

function applyLanguage(language: Language) {
  document.documentElement.lang = language;
  document.documentElement.dir = language === 'fa' ? 'rtl' : 'ltr';
  document.title = i18n.t('meta.title', { lng: language });
  window.localStorage.setItem(storageKey, language);
  void i18n.changeLanguage(language);
}

export const useLanguageStore = create<LanguageState>((set) => {
  const initialLanguage = getInitialLanguage();
  applyLanguage(initialLanguage);

  return {
    language: initialLanguage,
    setLanguage: (language) => {
      applyLanguage(language);
      set({ language });
    },
    toggleLanguage: () =>
      set((state) => {
        const nextLanguage = state.language === 'fa' ? 'en' : 'fa';
        applyLanguage(nextLanguage);
        return { language: nextLanguage };
      }),
  };
});
