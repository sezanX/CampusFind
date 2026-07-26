# 🎓 CampusFind

A modern, native Android application designed to help university students and staff easily report, search, and recover lost and found items.

<img src="app/src/main/res/drawable/app_logo.png" alt="CampusFind App Icon" width="150" />
<!--
![CampusFind App Icon](app/src/main/res/drawable/app_logo.png)
-->

## 🚀 Features

- 🔐 **Google Sign-In Authentication**: Instant and secure login via Firebase Auth & Credential Manager API.
- 📱 **2-Column Bento Grid Feed**: Responsive 2-column visual grid for browsing lost & found posts.
- 🔍 **Realtime Search & Filter Chips**: Filter posts instantaneously by keyword and type (*All*, *Lost*, *Found*).
- ☁️ **Free Cloud Image Hosting**: Integrated with ImgBB API for zero-cost image uploads without credit card requirements.
- ⚡ **Firebase Cloud Firestore**: Realtime NoSQL cloud database for post creation and instant sync.
- 📍 **Item Details & Parallax Banner**: Immersive parallax image header with an overlapping bottom-sheet layout.
- 💬 **Direct Owner Contact**: One-tap **Contact via Email** (`Intent.ACTION_SENDTO`) & **Contact via WhatsApp** (`wa.me` integration).
- 👤 **My Posts & Deletion Manager**: Dedicated profile tab displaying user posts with a one-tap deletion option.
- 🔔 **Push Notifications**: Receive alerts whenever new items are posted using Firebase Cloud Messaging (FCM).
- 🎨 **Material 3 Design System**: Full Edge-to-Edge transparent status bar rendering with custom Indigo & Slate palette (`#4F46E5`).


## 📚 Complete Workflow Documentation

For full architectural diagrams, sequence flows, data models, directory structures, and setup workflows, please see the **[WORKFLOW.md](WORKFLOW.md)** file.


## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **Architecture**: MVVM (Model-View-ViewModel), Coroutines, StateFlow
- **Backend Services**: Firebase Auth, Firebase Cloud Firestore, Firebase FCM
- **Networking**: Retrofit 2 & OkHttp 4 (ImgBB API)
- **Image Engine**: Glide 4 (with 300ms crossfade transitions)
- **UI Framework**: Material Components 3, View Binding, Jetpack Navigation Component


## 📦 Installation & Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/CampusFind.git
   ```
2. **Open in Android Studio**: Open Android Studio and select **Open** > navigate to the cloned folder.
3. **Add Firebase Config**: Place your `google-services.json` file inside the `app/` directory.
4. **Sync & Run**: Click **Sync Project with Gradle Files** (elephant icon) and click the green **Run** button (`Shift + F10`).


## 📄 License

This project is open-source under the MIT License.
