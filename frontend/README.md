# Frontend Structure

This frontend is a React + Vite + Tailwind app that is built into the Spring Boot static resources.

## Main Folders

- `src/api`: API clients for Spring Boot endpoints.
- `src/app`: app-level providers, layouts, and router configuration.
- `src/features`: feature-specific UI, data, hooks, and logic. Keep feature code here when it belongs to one product area.
- `src/pages`: route-level pages.
- `src/shared`: reusable UI, state, utilities, and constants used across features.
- `src/types`: shared TypeScript API/entity types.
- `public`: static assets served as-is by Vite and Spring Boot after build.

## App Layer

- `src/app/layouts`: global layouts such as `AppShell`.
- `src/app/providers`: global providers and app-level clients.
- `src/app/router`: route definitions.

## Shared Layer

- `src/shared/ui`: reusable UI components and their barrel exports.
- `src/shared/lib`: framework-agnostic helpers and shared constants.
- `src/shared/state`: global lightweight state stores.

## Feature Layer

Create a folder per feature:

```text
src/features/<feature-name>/
  api/
  components/
  data/
  hooks/
  model/
```

Only add a subfolder when it is needed.

## Assets

- Put public images in `public/images`.
- Put public fonts in `public/fonts`.
- Put component-scoped assets in `src/assets` when they should be imported and bundled.

## Rules

- Do not create new top-level folders for one-off components.
- Keep route registration in `src/app/router`.
- Keep global layout decisions in `src/app/layouts`.
- Keep shared components generic; feature-specific UI belongs in `src/features`.
- Prefer theme CSS variables from `src/styles/globals.css` over hard-coded colors.

## Commands

Use Node.js `>=20.19.0`.

```bash
npm install
npm run dev
npm run build
```

## Build with Java / Maven

See [`docs/FRONTEND-BUILD.md`](../docs/FRONTEND-BUILD.md) for full instructions (Persian).

Quick start from project root:

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```
