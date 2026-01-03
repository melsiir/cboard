# cboard - Custom Programming Keyboard

cboard is an Android keyboard app designed specifically for programming on mobile devices. It features customizable layouts with extra rows for programming symbols, adjustable keyboard height and button sizes, and follows Material 3 design guidelines.

## Features

- **Programming-focused layout**: Extra rows with programming symbols like `{ } [ ] ( ) ; : , . ? ! @ # $ % ^ & * | \ < > = + - * /`
- **Customizable layout**: Add, remove, or modify keyboard rows and keys as needed
- **Adjustable settings**: Customize keyboard height and button sizes
- **Export/Import settings**: Save and restore your custom configurations
- **Material 3 design**: Follows Google's latest design guidelines
- **System theme support**: Automatically follows system light/dark mode and accent colors
- **No text suggestions**: Pure programming keyboard without distracting suggestions

## Build Instructions

### Prerequisites

- Android SDK (API level 34)
- Android Build Tools 34.0.0 or higher
- Java 17 or higher
- Gradle 8.0 or higher

### Setting up the Project

1. Clone or download this repository
2. Open the project in Android Studio
3. Sync the project with Gradle files

### Building the App

#### Using Android Studio:
1. Open the project in Android Studio
2. Select "Build" → "Make Project" or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

#### Using Command Line:
1. Navigate to the project directory
2. Run the following command:
   ```bash
   ./gradlew build
   ```
   On Windows, use:
   ```cmd
   gradlew.bat build
   ```

### Building APK
To generate a debug APK:
```bash
./gradlew assembleDebug
```

The APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installation

1. Enable "Install from Unknown Sources" in your device settings
2. Transfer the APK file to your Android device
3. Open the APK file and follow the installation prompts

## Usage

1. After installation, go to Settings > System > Languages & Input > On-screen keyboard
2. Enable "cboard" keyboard
3. Select "cboard" as your default input method
4. Use the keyboard in any text field

## Customization

- Access settings via the main app or through Android's keyboard settings
- Adjust keyboard height and button sizes to your preference
- Add or modify custom keyboard layouts
- Export/import your settings to backup or share configurations

## GitHub Actions

The project includes a GitHub Actions workflow that automatically builds the app when changes are pushed to the main branch. The workflow:
- Sets up JDK 17
- Configures Android SDK
- Builds the app
- Runs tests
- Generates debug APK
- Uploads the APK as an artifact

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.