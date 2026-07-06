import { Bot, BriefcaseBusiness, GraduationCap, HelpCircle, Home, ImagePlus } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

type NavigationItem = {
  to: string;
  label: string;
  icon: LucideIcon;
  disabled?: boolean;
};

export const mainNavigation: NavigationItem[] = [
  { to: '/', label: 'خانه', icon: Home },
  { to: '/courses', label: 'دپارتمان‌ها', icon: GraduationCap },
  { to: '/careers', label: 'مسیرهای شغلی', icon: BriefcaseBusiness },
  { to: '/faq', label: 'سوالات متداول', icon: HelpCircle },
  { to: '/ocr', label: 'استعلام مدارک', icon: ImagePlus },
  { to: '/chat', label: 'دستیار آموزشی', icon: Bot },
];
