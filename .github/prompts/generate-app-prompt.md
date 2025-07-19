# Android Project Generation: OneVault

You are an expert Android application developer. Your task is to generate a complete, native Android application named "OneVault" from scratch by following these steps sequentially.

**Project Goal:** A secure, local-first Android application with a Bill Tracker, Password Manager, and File Vault. The app must follow Clean Architecture principles with an MVVM pattern for the presentation layer.

---

## Step 1: Project Setup and Dependencies

**Goal:** Create the initial Android project and configure all necessary dependencies.

**Instructions:**

1.  Create a new Android project in Android Studio using the "Empty Activity" template with Jetpack Compose.
2.  In the app-level `build.gradle.kts` file, add the following dependencies:
    * **Jetpack Compose:**
        * `androidx.compose.ui:ui`, `androidx.compose.ui:ui-graphics`, `androidx.compose.ui:ui-tooling-preview`, `androidx.compose.material3:material3`
        * `androidx.activity:activity-compose`
        * `androidx.lifecycle:lifecycle-viewmodel-compose`
        * `androidx.navigation:navigation-compose`
    * **Koin (Dependency Injection):**
        * `io.insert-koin:koin-android`
        * `io.insert-koin:koin-androidx-compose`
    * **Coroutines:**
        * `org.jetbrains.kotlinx:kotlinx-coroutines-android`
    * **SQLDelight (Local Database):**
        * `app.cash.sqldelight:android-driver`
        * `app.cash.sqldelight:coroutines-extensions`
    * **Ktor (Networking):**
        * `io.ktor:ktor-client-android`
        * `io.ktor:ktor-client-content-negotiation`
        * `io.ktor:ktor-serialization-kotlinx-json`
    * **Serialization:**
        * `org.jetbrains.kotlinx:kotlinx-serialization-json`
    * **Google ML Kit (OCR):**
        * `com.google.android.gms:play-services-mlkit-text-recognition`
3.  Configure the `sqldelight` and `kotlinx-serialization` Gradle plugins in your project.

---

## Step 2: Architecture and Package Structure

**Goal:** Set up a Clean Architecture structure within the Android module.

**Instructions:**

Create the following top-level packages in your main source set (`src/main/java/com/yourpackage/onevault`):
* **`data`**: For repository implementations, data sources (local/remote), and data transfer objects (DTOs).
* **`domain`**: For business logic, including use cases, domain models (entities), and repository interfaces. This is a pure Kotlin layer.
* **`presentation`**: For UI-related classes, including Compose screens, ViewModels (MVVM), UI state, and navigation.
* **`di`**: For Koin dependency injection modules.
* **`core`**: For utility classes, constants, and extensions used across the app.

---

## Step 3: Domain Layer

**Goal:** Define the core business logic, models, and contracts.

**Instructions:**

1.  Inside the `domain` package, create a `model` sub-package and define the following entity data classes. Annotate them with `@Serializable`.
    * `Bill(id: Long, title: String, category: String, amount: Double, vendor: String, billDate: Long, imagePath: String)`
    * `Credential(id: Long, serviceName: String, username: String, encryptedPassword: String, category: String)`
    * `VaultFile(id: Long, name: String, category: String, filePath: String)`
2.  Inside `domain`, create a `repository` sub-package and define repository interfaces with suspend functions for CRUD operations (e.g., `getBills(): Flow<List<Bill>>`, `suspend fun addBill(bill: Bill)`).
    * `BillRepository`
    * `CredentialRepository`
    * `VaultFileRepository`
3.  Inside `domain`, create a `usecase` sub-package. This is where your business logic will reside (e.g., `AddBillUseCase`).

---

## Step 4: Data Layer

**Goal:** Implement data sources, repositories, and data handling logic.

**Instructions:**

1.  **Local Database (SQLDelight):**
    * In `src/main/sqldelight`, create the directory for your database files.
    * Create `.sq` files for `BillEntity`, `CredentialEntity`, and `VaultFileEntity` with `CREATE TABLE` statements and queries for all CRUD operations.
    * In the `data` package, create a `local` sub-package to hold the SQLDelight database driver setup.
2.  **Secure Storage:**
    * In the `data` package, create a `security` sub-package.
    * Implement a `SecurityManager` class that uses the **Android Keystore system** to securely generate, store, and retrieve an encryption key for encrypting passwords.
3.  **Repository Implementations:**
    * In the `data` package, create repository implementations (e.g., `BillRepositoryImpl`) that depend on the SQLDelight database and implement the corresponding domain interfaces.

---

## Step 5: Presentation Layer (MVVM & Compose)

**Goal:** Build the UI, ViewModels, and navigation flow.

**Instructions:**

1.  **Navigation:**
    * In the `presentation` package, create a `navigation` sub-package.
    * Define a `NavGraph.kt` file to set up the app's navigation graph using `NavHost` from Jetpack Navigation Compose. Define routes for all screens.
2.  **ViewModels:**
    * For each feature, create a `ViewModel` (e.g., `BillTrackerViewModel`).
    * The ViewModel should expose UI state via a `StateFlow` and handle user events with public functions. It will interact with Use Cases from the domain layer.
3.  **UI Screens (Compose):**
    * Create a Composable function for each screen (e.g., `BillListScreen`).
    * The screen should be stateless if possible, observing the state from the ViewModel and delegating all user actions to the ViewModel.

---

## Step 6: Dependency Injection (Koin)

**Goal:** Wire up all the components using Koin.

**Instructions:**

1.  In the `di` package, create modules for each layer: `appModule`, `viewModelModule`, `useCaseModule`, `repositoryModule`.
2.  Define how to provide every dependency, from repositories and use cases to ViewModels and the SQLDelight database instance.
3.  Create a custom `Application` class for your app and initialize Koin there.

---

## Step 7: Feature Implementation (Bill Tracker & OCR)

**Goal:** Build the complete Bill Tracker feature.

**Instructions:**

1.  Implement the `AddBillUseCase` and `GetBillsUseCase`.
2.  Connect the `BillTrackerViewModel` to these use cases.
3.  Build the UI for listing bills and adding a new one.
4.  For the "Scan Bill" feature, use the `ActivityResultLauncher` to open the camera.
5.  On receiving the image URI, use the **Google ML Kit Text Recognition API** to process the image and extract text. The result should be sent to the ViewModel to pre-fill the form fields.

---

## Step 8: Feature Implementation (Password Manager)

**Goal:** Build the complete Password Manager feature.

**Instructions:**

1.  Implement the necessary Use Cases (e.g., `AddCredentialUseCase`).
2.  Create the `PasswordManagerViewModel`.
3.  Build the UI for listing and adding credentials.
4.  In the `AddCredentialUseCase` or `CredentialRepositoryImpl`, use the `SecurityManager` to encrypt the user's password string *before* saving it to the database.

---

## Step 9: Feature Implementation (File Vault & Import/Export)

**Goal:** Build the File Vault and data backup features.

**Instructions:**

1.  **File Vault:**
    * Use the `ActivityResultLauncher` with `ActivityResultContracts.OpenDocument` to let the user pick a file.
    * When a file URI is returned, open an `InputStream` and copy the file to your app's private internal storage.
    * Save the file's metadata (name, new path) to the database.
2.  **Import/Export:**
    * Use the `ActivityResultLauncher` with `ActivityResultContracts.CreateDocument` (for export) and `OpenDocument` (for import).
    * **Export:** Fetch data from the repository, serialize it to a JSON string using `kotlinx.serialization`, and write it to the user-selected file URI.
    * **Import:** Read the JSON from the user-selected file URI, deserialize it into a list of domain models, and save them to the database, checking for duplicates.