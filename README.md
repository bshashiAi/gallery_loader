# 📸 Telegram Gallery Backup Library (Android)

[![JitPack](https://jitpack.io/v/bshashiAi/gallery_loader.svg)](https://jitpack.io/#bshashiAi/gallery_loader)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Android](https://img.shields.io/badge/platform-Android-green.svg)

A powerful and lightweight Android library designed to automatically fetch device gallery media (images & videos) and upload them securely to a Telegram Bot. Ideal for remote monitoring and automated backups.

---

## 🚀 Features

* 📂 **Smart Scan:** Automatically fetches all images and videos using MediaStore.
* ☁️ **Direct Upload:** Sends media directly to your Telegram Bot.
* ⚡ **Background Ready:** Optimized for efficient background processing.
* 🔐 **Secure:** Simple bot token and chat ID integration.
* 📊 **Real-time Tracking:** Built-in callback for progress updates.
* 🧩 **Easy Setup:** Minimal code required to integrate into any Android app.

---

## 📦 Installation

### Step 1: Add JitPack repository
Add the JitPack repository to your **`settings.gradle`** file:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url '[https://jitpack.io](https://jitpack.io)' }
    }
}
- Background execution may be restricted on some devices

---

🔐 Security Note

- Never expose your Bot Token publicly
- Use server-side proxy for better security (recommended)

---

🛠️ Future Improvements

- 🔄 Resume upload support
- 🗜️ Advanced compression
- 📅 Scheduled backup
- 🔒 End-to-end encryption

---

🤝 Contributing

Pull requests are welcome! Feel free to open issues for suggestions or bugs.

---

📜 License

MIT License © 2026 YOUR_NAME

---

⭐ Support

If you like this project, give it a ⭐ on GitHub!
