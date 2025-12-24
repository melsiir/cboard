# cboard - Custom Programming Keyboard for Android

cboard is a custom Android keyboard specifically designed for programming on mobile devices. It features specialized layouts and customization options to make coding more efficient on smartphones and tablets.

## Features

- **Programming-focused layouts**: Dedicated rows for programming symbols like braces, brackets, and special characters
- **Customizable layouts**: Add, remove, or swap characters in any row to match your coding style
- **Adjustable settings**: Change keyboard height and button size to your preference
- **Space-aware typing**: Smart features like double-space to period and auto-bracket completion
- **Export/Import settings**: Save and restore your custom keyboard configurations
- **Swipe gestures**: Quickly switch between normal and programming layouts

## Installation

1. Clone this repository
2. Build the project using Android Studio or Gradle
3. Install the APK on your Android device
4. Enable the keyboard in your device's settings (Settings > System > Languages & Input > On-screen keyboards)
5. Select cboard as your default keyboard when prompted

## Building the Project

### Local Build
1. Install Android Studio and Android SDK
2. Open the project in Android Studio
3. Build the project (Build > Build Bundle(s) / APK(s) > Build APK(s))

### GitHub Actions Build
The project includes a GitHub Actions workflow that automatically builds the APK when changes are pushed to the repository. The APKs will be available in the "Actions" tab as build artifacts.

## Usage

- **Switch layouts**: Swipe left for programming layout, swipe right for normal layout, or tap the "Prog" key
- **Access settings**: Long-press the settings key to adjust keyboard height, button size, and other options
- **Customize layout**: Use the Layout Editor in settings to add or modify keys

## Customization

The keyboard supports custom layouts that can be created and managed through the Layout Editor. You can:

- Add custom characters to any row
- Remove existing keys
- Swap positions of keys
- Create multiple custom layouts for different programming languages

## Configuration

All settings are stored in SharedPreferences and can be exported/imported as text files for backup or sharing between devices.

## Development

The project follows standard Android Input Method Service patterns. Key components:

- `CBoardKeyboardService`: Main keyboard service handling input
- `LayoutManager`: Manages different keyboard layouts
- `SettingsManager`: Handles user preferences
- `SpaceAwareManager`: Implements space-aware typing features

## GitHub Actions Workflow

The project includes a GitHub Actions workflow (`.github/workflows/android.yml`) that automatically builds debug and release APKs when changes are pushed to the repository. The workflow includes:

- JDK 11 setup
- Android SDK setup
- Gradle wrapper validation
- Debug and release APK builds
- Artifact upload for download

## Contributing

Feel free to fork this repository and submit pull requests for improvements. This keyboard is designed specifically for the needs of mobile developers and welcomes contributions that enhance the programming experience on Android.

## License

This project is released under the MIT License.