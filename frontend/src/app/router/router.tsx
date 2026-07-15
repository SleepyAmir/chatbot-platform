import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from '../layouts/AppShell';
import { CareerDetailPage } from '../../pages/CareerDetailPage';
import { CareersPage } from '../../pages/CareersPage';
import { ChatPage } from '../../pages/ChatPage';
import { CourseDetailPage } from '../../pages/CourseDetailPage';
import { CoursesPage } from '../../pages/CoursesPage';
import { DashboardPage } from '../../pages/DashboardPage';
import { NotFoundPage } from '../../pages/NotFoundPage';
import { OcrPage } from '../../pages/OcrPage';
import { QaPage } from '../../pages/QaPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'courses', element: <CoursesPage /> },
      { path: 'courses/:id', element: <CourseDetailPage /> },
      { path: 'careers', element: <CareersPage /> },
      { path: 'careers/:id', element: <CareerDetailPage /> },
      { path: 'faq', element: <QaPage /> },
      { path: 'ocr', element: <OcrPage /> },
      { path: 'chat', element: <ChatPage /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
