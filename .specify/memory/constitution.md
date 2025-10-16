<!--
Sync Impact Report
- Version change: 0.0.0 → 1.0.0
- Modified principles: All (template → concrete)
- Added sections: Constraints, Development Workflow
- Removed sections: None
- Templates requiring updates: plan-template.md (✅), spec-template.md (✅), tasks-template.md (✅)
- Follow-up TODOs: TODO(RATIFICATION_DATE): Original adoption date unknown, set to today
-->

# OneVault Constitution

## Core Principles

### I. Privacy First
All user data MUST remain on the device by default. No data is transmitted to external servers or third parties. Features that require network access MUST be opt-in and clearly disclosed. Rationale: User privacy is fundamental to trust and app adoption.

### II. Data Security
All sensitive data (transactions, credentials, vault files) MUST be encrypted at rest using strong, industry-standard algorithms. Biometric authentication MUST be supported for unlocking the app. Rationale: Protecting user data from unauthorized access is non-negotiable.

### III. Code Quality
All code MUST follow clean architecture, MVVM, and Jetpack Compose best practices. Every feature MUST include unit and integration tests. Code reviews are mandatory before merging. Rationale: High code quality ensures maintainability, reliability, and scalability.

### IV. Performance
The app MUST remain responsive, with all critical user interactions completing within 200ms. Local database operations MUST be optimized for speed and efficiency. Rationale: Fast performance is essential for a positive user experience and trust.

### V. User Experience Consistency
UI/UX MUST follow Material Design 3 guidelines and maintain consistent navigation, theming, and interaction patterns. All user-facing text and flows MUST be clear and accessible. Rationale: Consistency reduces user confusion and increases satisfaction.

## Constraints & Standards
- The technology stack is fixed: Kotlin, Jetpack Compose, Coroutines, SQLDelight, Koin, ML Kit, Coil, DataStore, AndroidX Biometric.
- All data is stored locally and encrypted.
- No cloud sync or remote storage is permitted unless explicitly approved by governance.
- Accessibility and localization MUST be considered in all UI features.

## Development Workflow
- All code changes require peer review and MUST pass all tests.
- Features are developed in feature branches and merged via pull requests.
- Every feature and bugfix MUST include a plan, specification, and task breakdown using the provided templates.
- Constitution compliance is checked at the start and end of each feature cycle.

## Governance
- This constitution supersedes all other development practices.
- Amendments require documentation, team approval, and a migration plan if breaking changes are introduced.
- Versioning follows semantic rules: MAJOR for breaking/removal, MINOR for new/expanded, PATCH for clarifications.
- Compliance is reviewed quarterly or upon major release.

**Version**: 1.0.0 | **Ratified**: 2025-10-16 | **Last Amended**: 2025-10-16
