import {
  Award,
  Bot,
  BriefcaseBusiness,
  CalendarDays,
  FileCheck2,
  FlaskConical,
  GraduationCap,
  Languages,
  MonitorCog,
  UsersRound,
} from 'lucide-react';

export const departmentTags = [
  'فناوری اطلاعات و ارتباطات',
  'علوم مهندسی',
  'زبان‌های خارجی',
  'مدیریت و کسب‌وکار',
  'کودک و نوجوان',
  'هنر و رسانه',
];

export const heroCards = [
  {
    title: 'دپارتمان علوم مهندسی',
    subtitle: 'مسیرهای مهارت‌محور برای ورود به بازار کار',
    image: 'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=600&q=80',
  },
  {
    title: 'مشاوره آموزشی هوشمند',
    subtitle: 'انتخاب دوره بر اساس علاقه، مهارت و هدف شغلی',
    image: 'https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=600&q=80',
  },
  {
    title: 'مسیر شغلی پیشنهادی',
    subtitle: 'اتصال دوره‌ها به فرصت‌های واقعی بازار کار',
    image: 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&w=600&q=80',
  },
];

export const quickServices = [
  {
    title: 'تقویم آموزشی',
    description: 'برنامه دوره‌ها و شروع کلاس‌ها در یک نگاه',
    icon: CalendarDays,
    tone: 'var(--color-primary)',
  },
  {
    title: 'استعلام مدارک',
    description: 'بررسی اعتبار مدارک آموزشی با OCR و API',
    icon: FileCheck2,
    tone: 'var(--color-accent)',
  },
  {
    title: 'نمایندگی‌ها',
    description: 'دسترسی سریع به شعب و مراکز فعال آموزشی',
    icon: UsersRound,
    tone: 'var(--color-success)',
  },
  {
    title: 'هوش مصنوعی اجرا',
    description: 'راهنمای هوشمند برای دوره، مسیر و شغل',
    icon: Bot,
    tone: 'var(--color-warning)',
  },
];

export const departments = [
  {
    title: 'فناوری اطلاعات و ارتباطات',
    description: 'برنامه‌نویسی، شبکه، امنیت، هوش مصنوعی و مهارت‌های دیجیتال',
    icon: MonitorCog,
  },
  {
    title: 'علوم مهندسی',
    description: 'نرم‌افزارهای تخصصی، طراحی صنعتی و مهندسی کاربردی',
    icon: FlaskConical,
  },
  {
    title: 'زبان‌های خارجی',
    description: 'دوره‌های عمومی، تخصصی و آمادگی آزمون‌های بین‌المللی',
    icon: Languages,
  },
  {
    title: 'دانش سلامت',
    description: 'مهارت‌های کاربردی در حوزه سلامت، بهداشت و سبک زندگی',
    icon: Award,
  },
  {
    title: 'مسیرهای شغلی',
    description: 'اتصال آموزش به بازار کار با پیشنهاد شغل‌های مرتبط',
    icon: BriefcaseBusiness,
  },
  {
    title: 'خدمات آموزشی',
    description: 'ثبت‌نام، پیگیری دوره‌ها، استعلام و پشتیبانی آموزشی',
    icon: GraduationCap,
  },
];
