# Feature Specification: Split Bill

## Overall Purpose
OneVault is a personal vault application designed to securely manage financial transactions, credentials, and important files. All user data is stored locally on the device, never transmitted to external servers, ensuring maximum privacy and offline usability. The app uses strong encryption for all sensitive data and supports biometric authentication. OneVault delivers a consistent, modern user experience following Material Design 3, making security and usability accessible to all users.

**Feature Branch**: `[005-split-bill]`
**Created**: 2025-10-16
**Status**: Draft
**Input**: Users want to divide a single transaction into multiple parts among people or categories, tracking individual contributions or reimbursements, with automatic calculation of each participant’s share.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Split a Transaction Among People (Priority: P1)
Users can select a transaction and split its total amount among multiple people, specifying names and amounts or using equal shares.

**Why this priority**: Enables accurate tracking of shared expenses and reimbursements.

**Independent Test**: Split a transaction among three people and verify each person’s share is calculated and displayed correctly.

**Acceptance Scenarios**:
1. **Given** a transaction, **When** the user splits it among people, **Then** the app calculates and displays each participant’s share.
2. **Given** the user edits a share, **When** the total is updated, **Then** all shares remain consistent and the total matches the transaction amount.

---

### User Story 2 - Split a Transaction by Category (Priority: P2)
Users can divide a transaction into multiple categories (e.g., part Food, part Transport), assigning amounts or percentages to each.

**Why this priority**: Supports accurate categorization of complex transactions.

**Independent Test**: Split a transaction into two categories and verify the correct amounts are assigned and reflected in analytics.

**Acceptance Scenarios**:
1. **Given** a transaction, **When** the user splits it by category, **Then** the app calculates and displays the split amounts for each category.
2. **Given** the user changes a category’s amount, **When** the total is updated, **Then** all splits remain consistent and the total matches the transaction amount.

---

### User Story 3 - Track Contributions and Reimbursements (Priority: P3)
The app tracks who paid, who owes, and who has been reimbursed for each split transaction.

**Why this priority**: Facilitates group expense management and settling up.

**Independent Test**: Record a split bill, mark reimbursements, and verify the app updates balances for each participant.

**Acceptance Scenarios**:
1. **Given** a split transaction, **When** reimbursements are recorded, **Then** the app updates each participant’s balance accordingly.

---

### User Story 4 - Automatic Share Calculation (Priority: P4)
The app automatically calculates equal shares or allows custom amounts for each participant or category.

**Why this priority**: Reduces manual calculation and errors.

**Independent Test**: Add participants and verify the app calculates equal shares; edit a share and verify the total remains correct.

**Acceptance Scenarios**:
1. **Given** multiple participants, **When** the user selects equal split, **Then** the app divides the total evenly.
2. **Given** the user enters custom amounts, **When** the total is updated, **Then** the app validates that the sum matches the transaction amount.

---

### Edge Cases
- What happens if the sum of splits does not match the transaction total?
- How does the app handle rounding errors in equal splits?
- What if a participant is removed after splitting?
- How are splits tracked for recurring transactions?
- How does the app behave offline or with a large number of participants?

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST allow users to split a transaction among multiple people or categories.
- **FR-002**: System MUST automatically calculate equal shares and allow custom amounts.
- **FR-003**: System MUST track individual contributions, owed amounts, and reimbursements for each split.
- **FR-004**: System MUST validate that the sum of splits matches the transaction total.
- **FR-005**: System MUST store all split data locally, encrypted at rest.
- **FR-006**: System MUST support offline use for all split bill features.
- **FR-007**: System MUST follow clean architecture, MVVM, and Compose best practices.
- **FR-008**: All features MUST include unit and integration tests.
- **FR-009**: All critical user interactions MUST complete within 200ms.
- **FR-010**: UI MUST follow Material Design 3 and be accessible.

### Key Entities
- **Transaction**: Represents a single expense or income, with support for split details.
- **SplitDetail**: Represents a portion of a transaction assigned to a person or category, with fields for participant, category, amount, and reimbursement status.
- **Participant**: Represents a person involved in a split transaction.

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: Users can split transactions among people or categories and track shares without network access.
- **SC-002**: The app automatically calculates and validates splits for 100% of transactions.
- **SC-003**: All split data remains local and encrypted.
- **SC-004**: 90% of users can split a bill and track reimbursements in under 1 minute.
- **SC-005**: Split bill operations complete in under 200ms with 20+ participants or categories.

