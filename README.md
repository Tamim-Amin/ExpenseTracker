# 💰 ExpenseTracker

A modern Android expense tracking application built with **Java** and **Firebase**, showcasing the implementation of **10 Gang of Four (GoF) Design Patterns**.

## 📱 Features

- **User Authentication** – Secure login and registration with Firebase Auth  
- **Add Expenses** – Track expenses with description, amount, category, and date  
- **Edit Expenses** – Modify existing expenses with undo capability  
- **Delete Expenses** – Remove unwanted expense entries  
- **View Totals** – See total expenses, daily expenses, and category breakdowns  
- **Real-time Sync** – Data persisted to Firebase Realtime Database  
- **Expense Analysis** – Calculate and analyze expenses using various strategies  

## 🛠️ Tech Stack

- **Language:** Java  
- **Platform:** Android (Min SDK 24, Target SDK 36)  
- **Backend:** Firebase Authentication & Realtime Database  
- **Build Tool:** Gradle (Kotlin DSL)  
- **Testing:** JUnit 5, Mockito  

## 🎨 Design Patterns Implemented

This project demonstrates the practical application of **10 design patterns**, with contributors credited for their implementations.

### 1. Singleton Pattern 🔒 *(Tamim)*
**Location:** `patterns/singleton/FirebaseManager.java`  
Ensures a single instance of Firebase services throughout the app.

### 2. Observer Pattern 👁️ *(Sadia)*
**Location:** `patterns/observer/`  
Implements publish-subscribe mechanism for expense data updates.

### 3. Strategy Pattern 🎲 *(Sadia)*
**Location:** `patterns/strategy/`  
Enables interchangeable expense calculation algorithms at runtime.

### 4. Factory Pattern 🏭 *(Tahia)*
**Location:** `patterns/factory/`  
Encapsulates the creation of expense components, decoupling client code from class instantiation.

### 5. Composite Pattern 🌳 *(Tahia)*
**Location:** `patterns/composite/`  
Represents expenses hierarchically using tree structures.

### 6. Adapter Pattern 📦 *(Tamim)*
**Location:** `patterns/adapter/`  
Allows integration of external expense data in incompatible formats.

### 7. Facade Pattern 🏛️ *(Tamim)*
**Location:** `patterns/facade/ExpenseAnalysisFacade.java`  
Provides a simplified interface to complex subsystems.

### 8. Command Pattern 🎯 *(Tamim)*
**Location:** `patterns/command/`  
Encapsulates expense operations as commands, enabling flexible execution and undo support.

### 9. Iterator Pattern 🔄 *(Sadia)*
**Location:** `patterns/iterator/`  
Provides a way to traverse expense collections without exposing internal structures.

### 10. Memento Pattern 💾 *(Tahia)*
**Location:** `patterns/memento/`  
Enables undo functionality by capturing and restoring object state.

## 📊 Pattern Classification

| Pattern   | Type        | Purpose                     | Contributor |
|-----------|-------------|-----------------------------|-------------|
| Singleton | Creational  | Single Firebase instance     | Tamim       |
| Factory   | Creational  | Create expense components    | Tahia       |
| Adapter   | Structural  | Integrate external data      | Tamim       |
| Composite | Structural  | Expense hierarchies          | Tahia       |
| Facade    | Structural  | Simplify subsystem access    | Tamim       |
| Observer  | Behavioral  | Data change notifications    | Sadia       |
| Strategy  | Behavioral  | Calculation algorithms       | Sadia       |
| Command   | Behavioral  | Encapsulate operations       | Tamim       |
| Iterator  | Behavioral  | Collection traversal         | Sadia       |
| Memento   | Behavioral  | Undo functionality           | Tahia       |

## 📁 Project Structure



## 📁 Project Structure

```
app/src/main/java/com/example/expensetracker/
├── MainActivity.java          # Main dashboard
├── AddExpenseActivity.java    # Add new expenses
├── EditExpenseActivity.java   # Edit existing expenses
├── LoginActivity.java         # User login
├── RegisterActivity.java      # User registration
├── ExpenseCard.java           # Expense card component
└── patterns/
    ├── adapter/               # Adapter Pattern
    ├── command/               # Command Pattern
    ├── composite/             # Composite Pattern
    ├── facade/                # Facade Pattern
    ├── factory/               # Factory Pattern
    ├── iterator/              # Iterator Pattern
    ├── memento/               # Memento Pattern
    ├── observer/              # Observer Pattern
    ├── singleton/             # Singleton Pattern
    └── strategy/              # Strategy Pattern
```

## 🚀 Getting Started

### Prerequisites
- Android Studio (Arctic Fox or later)
- JDK 11+
- Firebase project with Authentication and Realtime Database enabled

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Tamim-Amin/ExpenseTracker.git
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name `com.example.expensetracker`
   - Download `google-services.json` and place it in `app/` directory
   - Enable Email/Password authentication
   - Set up Realtime Database rules

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

## 🧪 Testing

Run unit tests with:
```bash
./gradlew test
```

The project uses JUnit 5 and Mockito for testing.

## 👥 Authors

- **Fawzia Tahia** – [GitHub](https://github.com/Fawziatahia)
- **Tamim Amin** – [GitHub](https://github.com/Tamim-Amin)
- **Sadiah Rahman** – [GitHub](https://github.com/Sadiah-Rahman)



## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🙏 Acknowledgments

- Gang of Four for the foundational design patterns
- Firebase for backend services
- Android development community
