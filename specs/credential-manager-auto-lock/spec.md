# Feature Specification: Credential Manager Auto-Lock

## Overall Purpose
OneVault is a personal vault application designed to securely manage financial transactions, credentials, and important files. All user data is stored locally on the device, never transmitted to external servers, ensuring maximum privacy and offline usability. The app uses strong encryption for all sensitive data and supports biometric authentication. OneVault delivers a consistent, modern user experience following Material Design 3, making security and usability accessible to all users.

**Feature Branch**: `[008-credential-manager-auto-lock]`
**Created**: 2025-10-16
**Status**: Draft
**Input**: Users want the Credential Manager to auto-lock after a period of inactivity or when the app is minimized, requiring re-authentication with the biometric.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Auto-Lock After Inactivity (Priority: P1)
The Credential Manager automatically locks if there is no user activity for a configurable period (e.g., 30 seconds, 1 minute, 5 minutes).

**Why this priority**: Protects sensitive credentials if the device is left unattended.

**Independent Test**: Unlock the vault, wait for the inactivity timeout, and verify that the vault locks and requires re-authentication.

**Acceptance Scenarios**:
1. **Given** the vault is unlocked, **When** the user is inactive for the configured period, **Then** the vault auto-locks and requires the biometric to unlock.

---

### User Story 2 - Auto-Lock on App Minimize (Priority: P2)
The Credential Manager automatically locks when the app is minimized, backgrounded, or closed.

**Why this priority**: Ensures credentials are not accessible if the app is left running in the background.

**Independent Test**: Unlock the vault, minimize the app, and verify that the vault locks and requires re-authentication when reopened.

**Acceptance Scenarios**:
1. **Given** the vault is unlocked, **When** the app is minimized or backgrounded, **Then** the vault auto-locks and requires the biometric to unlock.

---

### User Story 3 - Configurable Timeout (Priority: P3)
Users can set the inactivity timeout period for auto-lock (e.g., 30 seconds, 1 minute, 5 minutes).

**Why this priority**: Allows users to balance convenience and security according to their needs.

**Independent Test**: Change the timeout setting and verify that auto-lock occurs after the selected period.

**Acceptance Scenarios**:
1. **Given** the user sets a new timeout, **When** the vault is left inactive, **Then** auto-lock occurs after the selected period.

---

### Edge Cases
- What happens if the device time changes while the vault is unlocked?
- How does the app handle rapid minimize/restore cycles?
- What if the user is in the middle of editing a credential when auto-lock triggers?
- How does the app behave if the timeout is set to the minimum or maximum allowed value?
- How does the app handle biometric unlock in conjunction with auto-lock?

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST auto-lock the Credential Manager after a period of inactivity.
- **FR-002**: System MUST auto-lock the Credential Manager when the app is minimized, backgrounded, or closed.
- **FR-003**: System MUST require re-authentication with the biometric to unlock after auto-lock.
- **FR-004**: System MUST allow users to configure the inactivity timeout period.
- **FR-005**: System MUST store all settings and credential data locally, encrypted at rest.
- **FR-006**: System MUST support offline use for all auto-lock features.
- **FR-007**: System MUST follow clean architecture, MVVM, and Compose best practices.
- **FR-008**: All features MUST include unit and integration tests.
- **FR-009**: All critical user interactions and auto-lock operations MUST complete within 200ms.
- **FR-010**: UI MUST follow Material Design 3 and be accessible.

### Key Entities
- **Vault**: Represents the encrypted storage for credentials, with lock/unlock state and timeout settings.
- **AutoLockSettings**: Represents user-configurable auto-lock timeout values.

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: Vault auto-locks after the configured period of inactivity or when the app is minimized.
- **SC-002**: Credentials are inaccessible until the correct biometric is entered after auto-lock.
- **SC-003**: 90% of users can configure and use auto-lock without confusion or error.
- **SC-004**: Auto-lock and unlock operations complete in under 200ms.
