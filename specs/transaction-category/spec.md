# Feature Specification: Transaction Category Management

## Overall Purpose
OneVault is a personal vault application designed to securely manage financial transactions, credentials, and important files. All user data is stored locally on the device, never transmitted to external servers, ensuring maximum privacy and offline usability. The app uses strong encryption for all sensitive data and supports biometric authentication. OneVault delivers a consistent, modern user experience following Material Design 3, making security and usability accessible to all users.

**Feature Branch**: `[002-transaction-category-feature]`  
**Created**: 2025-10-16  
**Status**: Draft  
**Input**: Users want to create, rename, and delete categories for income and expenses. Each transaction is assigned to a category, and summary statistics are calculated by category and type.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create a Category (Priority: P1)
Users can create a new category for either income or expense, specifying a name, icon, and color.

**Why this priority**: Enables users to organize transactions according to their needs.

**Independent Test**: Create a new category and verify it appears in the category list and can be assigned to transactions.

**Acceptance Scenarios**:
1. **Given** the user is on the category management screen, **When** they create a new category, **Then** it is saved locally and available for selection.
2. **Given** a category with the same name exists, **When** the user tries to create it, **Then** an error is shown.

---

### User Story 2 - Rename or Delete a Category (Priority: P2)
Users can rename or delete existing categories. Deleting a category prompts the user to reassign or remove affected transactions.

**Why this priority**: Maintains data integrity and allows users to keep categories relevant.

**Independent Test**: Rename a category and verify the change is reflected everywhere. Delete a category and verify transactions are handled appropriately.

**Acceptance Scenarios**:
1. **Given** a category is renamed, **When** the user saves, **Then** all transactions using that category reflect the new name.
2. **Given** a category is deleted, **When** the user confirms, **Then** the app prompts to reassign or remove affected transactions.

---

### User Story 3 - Assign Categories to Transactions (Priority: P3)
Each transaction must belong to one category. Users can assign or change the category when creating or editing a transaction.

**Why this priority**: Ensures all transactions are organized and can be summarized by category.

**Independent Test**: Assign a category to a transaction and verify it appears in the transaction details and summary.

**Acceptance Scenarios**:
1. **Given** a transaction is created or edited, **When** a category is selected, **Then** the transaction is saved with that category.

---

### User Story 4 - View Summary Statistics by Category and Type (Priority: P4)
Users can view summary statistics (e.g., total amount, count) for each category, separated by income and expense.

**Why this priority**: Provides insight into spending and income patterns.

**Independent Test**: Add transactions in various categories and verify summary statistics are accurate.

**Acceptance Scenarios**:
1. **Given** transactions exist in multiple categories, **When** the user views the summary, **Then** totals and counts are correct for each category and type.

---

### Edge Cases
- What happens if a user tries to delete a category that is the only one for a type?
- How does the system handle duplicate category names?
- What if a category is deleted while transactions are being edited?
- How are default categories (e.g., "Uncategorized") handled?
- How does the app behave with a very large number of categories?

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: System MUST allow users to create, rename, and delete categories for both income and expenses.
- **FR-002**: System MUST prevent duplicate category names for the same type.
- **FR-003**: System MUST require each transaction to belong to one category.
- **FR-004**: System MUST prompt users to reassign or remove transactions when deleting a category.
- **FR-005**: System MUST store all category and transaction data locally, encrypted at rest.
- **FR-006**: System MUST provide summary statistics (total, count) by category and type.
- **FR-007**: System MUST support offline use for all category features.
- **FR-008**: System MUST follow clean architecture, MVVM, and Compose best practices.
- **FR-009**: All features MUST include unit and integration tests.
- **FR-010**: All critical user interactions MUST complete within 200ms.
- **FR-011**: UI MUST follow Material Design 3 and be accessible.

### Key Entities
- **TransactionCategory**: Represents a category for transactions, with fields for name, icon, color, type (income/expense), and parent category.
- **Transaction**: Represents a single income or expense, assigned to a category.

## Success Criteria *(mandatory)*

### Measurable Outcomes
- **SC-001**: Users can create, rename, and delete categories without network access.
- **SC-002**: No duplicate category names exist for the same type.
- **SC-003**: All transactions are assigned to a valid category at all times.
- **SC-004**: Summary statistics are accurate and update in real time.
- **SC-005**: Category management operations complete in under 200ms with 100+ categories.

