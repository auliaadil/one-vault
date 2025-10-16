# Feature Specification: Local Encrypted SQLite Database

## Overall Purpose
OneVault is a personal vault application designed to securely manage financial transactions, credentials, and important files. All user data is stored locally on the device, never transmitted to external servers, ensuring maximum privacy and offline usability. The app uses strong encryption for all sensitive data and supports biometric authentication. OneVault delivers a consistent, modern user experience following Material Design 3, making security and usability accessible to all users.

**Feature Branch**: `[009-local-encrypted-db]`
**Created**: 2025-10-16
**Status**: Draft
**Input**: Define a local encrypted SQLite database for Transactions and Credentials, supporting efficient indexing, local backups, and relationships between entities where needed.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Secure Local Storage (Priority: P1)
All Transactions and Credentials are stored in an encrypted local SQLite database, inaccessible to other apps or users without authentication.

**Why this priority**: Ensures privacy and security of sensitive data.

**Independent Test**: Add transactions and credentials, verify they are stored encrypted and cannot be accessed without authentication.

**Acceptance Scenarios**:
1. **Given** the app is installed, **When** the user adds data, **Then** it is stored in an encrypted local database.
2. **Given** the device is rooted or another app attempts access, **When** the database is opened, **Then** data remains inaccessible without the encryption key.

---

### User Story 2 - Efficient Indexing and Querying (Priority: P2)
The schema supports efficient indexing for fast queries on transaction date, category, and credential service name.

**Why this priority**: Ensures the app remains fast and responsive as data grows.

**Independent Test**: Add a large number of records and verify queries (e.g., by date, category, service) are fast (<200ms).

**Acceptance Scenarios**:
1. **Given** 1000+ transactions, **When** the user searches or filters, **Then** results are returned quickly.
2. **Given** 100+ credentials, **When** the user searches by service name, **Then** results are returned quickly.

---

### User Story 3 - Local Backups and Restore (Priority: P3)
Users can create encrypted local backups of the database and restore from them as needed.

**Why this priority**: Protects against data loss and supports device migration.

**Independent Test**: Create a backup, uninstall/reinstall the app, and restore the backup to recover all data.

**Acceptance Scenarios**:
1. **Given** a backup is created, **When** the app is reinstalled, **Then** the user can restore all data from the backup.

---

### User Story 4 - Entity Relationships (Priority: P4)
The schema supports relationships (e.g., transactions reference categories and accounts; credentials can be linked to password templates).

**Why this priority**: Enables rich data modeling and integrity.

**Independent Test**: Add related entities and verify foreign key constraints and cascading updates/deletes work as expected.

**Acceptance Scenarios**:
1. **Given** a transaction references a category, **When** the category is deleted, **Then** the transaction is updated or reassigned according to app rules.
2. **Given** a credential references a password template, **When** the template is updated, **Then** the credential reflects the change if applicable.

---

### Edge Cases
- What happens if the encryption key is lost or corrupted?
- How does the app handle schema migrations or upgrades?
- What if a backup is restored on a different device or app version?
- How are partial or failed restores handled?
- How does the app behave with very large datasets?

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST use an encrypted local SQLite database for all Transactions and Credentials.
- **FR-002**: System MUST support efficient indexing for fast queries on key fields (date, category, service name).
- **FR-003**: System MUST allow users to create and restore encrypted local backups of the database.
- **FR-004**: System MUST support foreign key relationships between entities (e.g., transactions and categories, credentials and templates).
- **FR-005**: System MUST enforce referential integrity and cascading updates/deletes where appropriate.
- **FR-006**: System MUST store all data and backups locally, encrypted at rest.
- **FR-007**: System MUST support offline use for all database features.
- **FR-008**: System MUST follow clean architecture, MVVM, and Compose best practices.
- **FR-009**: All features MUST include unit and integration tests.
- **FR-010**: All critical queries and backup/restore operations MUST complete within 200ms for typical datasets.
- **FR-011**: UI MUST follow Material Design 3 and be accessible.

### Key Entities
- **Transaction**: Stores financial activity, references category and account.
- **TransactionCategory**: Stores categories for transactions.
- **Account**: Stores account information for transactions.
- **Credential**: Stores user credentials, can reference a password template.
- **PasswordTemplate**: Stores reusable password generation rules.

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: All data is stored and remains encrypted at all times.
- **SC-002**: Queries and backup/restore operations complete in under 200ms with 1000+ records.
- **SC-003**: Users can create and restore backups without data loss or corruption.
- **SC-004**: All entity relationships are enforced and maintained correctly.

