# 💸 ShareFair - Premium Expense Sharing

**ShareFair** is a high-performance, visually stunning Android application built with **Jetpack Compose** and **Kotlin**. It redefines how you split expenses with friends, family, and colleagues, offering a seamless, animated, and modern user experience.

![Premium UI Splash](https://img.shields.io/badge/UI-Modern%20Premium-blueviolet)
![Tech Stack](https://img.shields.io/badge/Tech-Jetpack%20Compose-green)
![Language](https://img.shields.io/badge/Language-Kotlin-orange)

---

## ✨ Features

- 🎨 **Premium Aesthetic** — A hand-crafted design with glassmorphism, smooth gradients, and a curated color palette (Electric Violet & Deep Charcoal).
- 🎬 **Smooth Animations** — Entrance animations, tab transitions, and interactive UI elements that make the app feel alive.
- 🧾 **Smart Split** — Add expenses, assign participants, and split with ease.
- 👥 **Group Management** — Create tailored groups for trips, roommates, or events.
- 🔁 **Real-time Balances** — Modern interactive cards showing exactly who owes what.
- 🔒 **Secure Auth** — Integrated with **Firebase** and **Google Credential Manager** for one-tap sign-in.
- 🔔 **Intelligent Notifications** — Stay updated with a beautifully designed notification feed.

---

## 🛠 Tech Stack

- **Language:** Kotlin 1.9+  
- **UI Toolkit:** Jetpack Compose (100% Declarative)
- **Design System:** Material 3 with Custom Premium Theming
- **Authentication:** Firebase Auth & Google Credential Manager API
- **Architecture:** Clean Architecture with Separated Activity/Screen Logic
- **Animations:** Compose Animation API (`AnimatedVisibility`, `AnimatedContent`, `Animatable`)

---

## 📂 Project Architecture

The project has been refactored for maximum maintainability and scalability:

```text
com.kapasiya.sharefair/
├── model/          # Clean, singular data entities (Notification, Bill, etc.)
├── ui/
│   ├── theme/      # Centralized design system (Colors, Type, Theme)
│   ├── components/ # Reusable UI atoms (Custom TextFields, Buttons)
│   └── screens/    # Individual screen implementations (Login, Home, Profile)
└── (activities)    # Lifecycle & Navigation management
```

---

## 📲 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Kotlin 1.9.0+
- Android SDK 26+ (Integrated with Material You)

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/soyamkapasiya/ShareFair.git
   ```

2. **Add Firebase**
   - Connect your project to Firebase.
   - Add `google-services.json` to the `app/` directory.

3. **Run the App**
   - Clean and Rebuild project in Android Studio.
   - Run on an emulator or physical device.

---

## 📸 Premium Experience

| Login & Auth | Dashboard | Profile |
| :---: | :---: | :---: |
| Glassmorphic Inputs | Interactive Gradients | Clean Minimalist UI |
| One-Tap Google Auth | Smooth Tab Transitions | Animated Feedback |

---

## ✅ Ongoing Roadmap
- [x] Full Jetpack Compose Migration
- [x] Premium Theme & Animations
- [x] Google Credential Manager Integration
- [ ] 📈 Expense analytics and interactive charts
- [ ] 🧾 Export reports as PDF/CSV
- [ ] 💵 Multi-currency support

---

## 🤝 Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

## 📃 License
MIT License © 2026 Soyam Kapasiya

## 🙋‍♂️ Author
**Soyam Kapasiya**  
- GitHub: [@soyamkapasiya](https://github.com/soyamkapasiya)  
- LinkedIn: [Soyam Kapasiya](https://linkedin.com/in/soyamkapasiya)
