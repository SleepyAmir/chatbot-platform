import { Bot, BriefcaseBusiness, GraduationCap, HelpCircle, Home, ImagePlus } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

type NavigationItem = {
  to: string;
  labelKey: string;
  icon: LucideIcon;
  disabled?: boolean;
};

export const mainNavigation: NavigationItem[] = [
  { to: '/', labelKey: 'nav.home', icon: Home },
  { to: '/courses', labelKey: 'nav.departments', icon: GraduationCap },
  { to: '/careers', labelKey: 'nav.careers', icon: BriefcaseBusiness },
  { to: '/faq', labelKey: 'nav.faq', icon: HelpCircle },
  { to: '/ocr', labelKey: 'nav.ocr', icon: ImagePlus },
  { to: '/chat', labelKey: 'nav.assistant', icon: Bot },
];
