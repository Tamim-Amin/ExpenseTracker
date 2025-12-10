# 💰 ExpenseTracker

A modern Android expense tracking application built with **Java** and **Firebase**, showcasing the implementation of **10 Gang of Four (GoF) Design Patterns**.

## 📱 Features

- **User Authentication** - Secure login and registration with Firebase Auth
- **Add Expenses** - Track expenses with description, amount, category, and date
- **Edit Expenses** - Modify existing expenses with undo capability
- **Delete Expenses** - Remove unwanted expense entries
- **View Totals** - See total expenses, daily expenses, and category breakdowns
- **Real-time Sync** - Data persisted to Firebase Realtime Database
- **Expense Analysis** - Calculate and analyze expenses using various strategies

## 🛠️ Tech Stack

- **Language:** Java
- **Platform:** Android (Min SDK 24, Target SDK 36)
- **Backend:** Firebase Authentication & Realtime Database
- **Build Tool:** Gradle (Kotlin DSL)
- **Testing:** JUnit 5, Mockito

## 🎨 Design Patterns Implemented

This project demonstrates the practical application of **10 design patterns**:

### 1. Singleton Pattern 🔒
**Location:** `patterns/singleton/FirebaseManager.java`

Ensures a single instance of Firebase services throughout the app, providing centralized access to `FirebaseAuth` and `FirebaseDatabase`.

```java
public static synchronized FirebaseManager getInstance() {
    if (instance == null) {
        instance = new FirebaseManager();
    }
    return instance;
}
```

### 2. Observer Pattern 👁️
**Location:** `patterns/observer/`
- `ExpenseObserver.java` - Observer interface
- `ExpenseRepository.java` - Subject (Observable)

Implements publish-subscribe mechanism for expense data updates. `MainActivity` registers as an observer to receive notifications when expenses are loaded from Firebase.

### 3. Strategy Pattern 🎲
**Location:** `patterns/strategy/`
- `ExpenseStrategy.java` - Strategy interface
- `TotalExpenseStrategy.java` - Calculates total of all expenses
- `CategoryExpenseStrategy.java` - Calculates expenses by category
- `DailyExpenseStrategy.java` - Calculates expenses for a specific date
- `ExpenseCalculatorContext.java` - Context class

Enables interchangeable expense calculation algorithms at runtime.

### 4. Factory Pattern 🏭
**Location:** `patterns/factory/`
- `ExpenseComponentFactory.java` - Factory interface
- `ConcreteExpenseFactory.java` - Concrete factory implementation

Encapsulates the creation of expense components, decoupling client code from concrete class instantiation.

### 5. Composite Pattern 🌳
**Location:** `patterns/composite/`
- `ExpenseComponent.java` - Component interface
- `ExpenseLeaf.java` - Leaf node (single expense)
- `ExpenseGroup.java` - Composite (group of expenses)

Creates tree structures to represent expenses hierarchically, enabling uniform operations across individual expenses and groups.

### 6. Adapter Pattern 📦
**Location:** `patterns/adapter/`
- `ExternalExpense.java` - Adaptee (incompatible interface)
- `ExpenseAdapter.java` - Adapter

Allows integration of external expense data with different formats into the existing system.

### 7. Facade Pattern 🏛️
**Location:** `patterns/facade/ExpenseAnalysisFacade.java`

Provides a simplified interface to complex subsystems (Strategy and Composite patterns), hiding implementation complexity from client code.

### 8. Command Pattern 🎯
**Location:** `patterns/command/`
- `ExpenseCommand.java` - Command interface
- `AddExpenseCommand.java` - Concrete command
- `ExpenseInvoker.java` - Invoker
- `ExpenseReceiver.java` - Receiver

Encapsulates expense operations as command objects, enabling flexible execution and potential undo functionality.

### 9. Iterator Pattern 🔄
**Location:** `patterns/iterator/`
- `ExpenseIterator.java` - Iterator interface
- `ExpenseListIterator.java` - Concrete iterator

Provides a way to traverse expense collections without exposing the underlying data structure.

### 10. Memento Pattern 💾
**Location:** `patterns/memento/ExpenseMemento.java`

Enables undo functionality in expense editing by capturing and restoring an object's internal state.

## 📊 Pattern Classification

| Pattern | Type | Purpose |
|---------|------|---------|
| Singleton | Creational | Single Firebase instance |
| Factory | Creational | Create expense components |
| Adapter | Structural | Integrate external data |
| Composite | Structural | Expense hierarchies |
| Facade | Structural | Simplify subsystem access |
| Observer | Behavioral | Data change notifications |
| Strategy | Behavioral | Calculation algorithms |
| Command | Behavioral | Encapsulate operations |
| Iterator | Behavioral | Collection traversal |
| Memento | Behavioral | Undo functionality |

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

- **Fawzia Tahia ** - [GitHub](https://github.com/Fawziatahia)
- **Tamim Amin** - [GitHub](https://github.com/Tamim-Amin)


## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🙏 Acknowledgments

- Gang of Four for the foundational design patterns
- Firebase for backend services
- Android development community
