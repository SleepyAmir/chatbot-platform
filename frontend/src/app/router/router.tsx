import { createBrowserRouter } from 'react-router-dom';
import { AppShell } from '../layouts/AppShell';
import { CareerDetailPage } from '../../pages/CareerDetailPage';
import { CareersPage } from '../../pages/CareersPage';
import { ChatPage } from '../../pages/ChatPage';
import { CoursesPage } from '../../pages/CoursesPage';
import { DashboardPage } from '../../pages/DashboardPage';
import { OcrPage } from '../../pages/OcrPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'courses', element: <CoursesPage /> },
      { path: 'careers', element: <CareersPage /> },
      { path: 'careers/:id', element: <CareerDetailPage /> },
      { path: 'ocr', element: <OcrPage /> },
      { path: 'chat', element: <ChatPage /> },
    ],
  },
]);
