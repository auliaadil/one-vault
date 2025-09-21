# OneVault 🔐

A secure, local-first Android application for managing your financial bills, passwords, and important files. OneVault prioritizes your privacy by storing all data locally on your device with advanced encryption and biometric security.

## 📱 Features

### 💳 Bill Tracker
- **Smart Bill Management**: Track and organize your bills with categorization
- **OCR Scanning**: Scan physical bills using your camera with ML Kit text recognition
- **Account Integration**: Link bills to specific accounts and track balances
- **Attachment Support**: Store bill images for reference
- **Category Management**: Organize bills with customizable categories
- **Rupiah Currency Support**: Optimized for Indonesian currency formatting

### 🔑 Password Manager
- **Secure Credential Storage**: Store usernames and passwords safely
- **Template-Based Password Generation**: Create complex passwords using custom rules
- **Rule Engine**: Generate passwords based on service names and user accounts
- **Copy to Clipboard**: Quick access to credentials
- **Template System**: Reusable password generation templates

### 📁 File Vault
- **Document Storage**: Secure storage for important files
- **Import/Export**: Data backup and restore capabilities
- **File Organization**: Categorize and manage your documents

### 🛡️ Security Features
- **Biometric Authentication**: Fingerprint and face unlock support
- **App Lock**: Configurable timeout settings (5 seconds to 5 minutes)
- **Local Storage**: All data stored locally on device
- **Data Encryption**: Advanced encryption for sensitive information
- **Privacy First**: No cloud storage or data transmission

## 🏗️ Architecture

OneVault follows **Clean Architecture** principles with modern Android development practices:

### Tech Stack
- **UI**: Jetpack Compose with Material Design 3
- **Language**: Kotlin 100%
- **Database**: SQLDelight for type-safe SQL
- **DI**: Koin for dependency injection
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose
- **Security**: AndroidX Biometric
- **ML**: Google ML Kit for OCR
- **Image Loading**: Coil
- **Settings**: DataStore Preferences

### Project Structure
```
app/src/main/java/com/adilstudio/project/onevault/
├── core/                    # Core utilities and extensions
├── data/                    # Data layer (repositories, local storage)
├── di/                      # Dependency injection modules
├── domain/                  # Business logic and models
├── presentation/            # UI layer (Compose screens, ViewModels)
├── service/                 # Android services
└── ui/                      # Theme and styling
```

### Key Patterns
- **MVVM**: Model-View-ViewModel architecture
- **Repository Pattern**: Data abstraction layer
- **Use Cases**: Business logic encapsulation
- **Single Source of Truth**: Centralized state management
- **Reactive Programming**: Flow-based data streams

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK API 26+ (Android 8.0)
- Device with biometric capability (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/one-vault.git
   cd one-vault
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Build Variants
- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

## 📋 Requirements

### Minimum Requirements
- **Android Version**: 8.0 (API level 26)
- **RAM**: 2GB recommended
- **Storage**: 100MB for app installation
- **Camera**: Required for bill scanning feature
- **Biometric Hardware**: Optional, for enhanced security

### Supported Architectures
- ARM64 (arm64-v8a)
- x86_64

## 🔧 Configuration

### App Permissions
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
```

### Build Configuration
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 26
- **Version Code**: 1
- **Version Name**: 1.0

## 🧪 Testing

The project includes comprehensive testing:

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- Repository layer tests with Mockito
- ViewModel tests with coroutines testing
- Database tests with SQLDelight
- UI tests with Compose testing

## 📱 Features in Detail

### Bill Management
- Create, read, update, delete bills
- OCR text extraction from images
- Category assignment and management
- Account balance tracking
- Date-based organization
- Search and filter capabilities

### Password Management
- Secure credential storage with encryption
- Rule-based password generation
- Template system for reusable patterns
- Copy/paste functionality
- Service-specific organization

### Security
- Biometric authentication setup
- App lock with customizable timeout
- Local data encryption
- Secure key management
- Privacy-focused design

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards
- Follow Kotlin coding conventions
- Use meaningful commit messages
- Add unit tests for new features
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏢 Developer

**Adil Studio**
- Focused on privacy-first mobile applications
- Committed to local-first data storage
- Passionate aboÏut Android development

## 🔮 Roadmap

### Upcoming Features
- [ ] Dark theme support
- [ ] Advanced bill analytics and reporting
- [ ] Multi-language support
- [ ] Widget support for quick access
- [ ] File encryption in vault
- [ ] Export to various formats (CSV, PDF)

### Version History
- **v1.0.0** - Initial release with core features

## 📞 Support

For support, please create an issue in the GitHub repository or contact the development team.

---

**OneVault** - Your personal, secure, and private digital vault. 🔐✨
