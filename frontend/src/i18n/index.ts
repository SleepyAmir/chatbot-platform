import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { resources } from './resources';

const storedLanguage = window.localStorage.getItem('chatbot-platform-language');

void i18n.use(initReactI18next).init({
  resources,
  lng: storedLanguage === 'en' ? 'en' : 'fa',
  fallbackLng: 'fa',
  interpolation: { escapeValue: false },
});

export default i18n;
