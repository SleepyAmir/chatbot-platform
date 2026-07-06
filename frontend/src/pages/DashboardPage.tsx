import { AssistantCta } from '../features/landing/components/AssistantCta';
import { DepartmentsSection } from '../features/landing/components/DepartmentsSection';
import { HeroShowcase } from '../features/landing/components/HeroShowcase';
import { QuickServices } from '../features/landing/components/QuickServices';

export function DashboardPage() {
  return (
    <>
      <HeroShowcase />
      <QuickServices />
      <DepartmentsSection />
      <AssistantCta />
    </>
  );
}
