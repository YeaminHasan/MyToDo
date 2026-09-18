# MyToDo - Android Task Management App

A modern, clean, and efficient To-Do list application built with **Java** and **Room Database**. This project follows the **MVVM** (Model-View-ViewModel) design principles and provides a seamless user experience with Google Keep-inspired features.

## 🚀 Features

-   **Room Database Integration**: Persistent storage for all your tasks.
-   **Task Management**: Create, Read, Update, and Delete (CRUD) tasks easily.
-   **Priority System**: Categorize tasks as **High**, **Medium**, or **Low**.
-   **Google Keep Style Sorting**: 
    -   Active tasks are sorted by priority (High at the top).
    -   Completed tasks automatically move to the bottom.
-   **Visual Feedback**:
    -   Strikethrough effect and dimmed opacity for completed tasks.
    -   Color-coded priority badges (Red for High, Orange for Medium, Green for Low).
-   **Modern UI**:
    -   Dark Theme support with custom orange-accented icons.
    -   **Bottom Sheet Detail View**: Read full task descriptions without cluttering the screen.
    -   **Floating Action Button (FAB)**: Quick access to add new tasks.
-   **Optimized Performance**: Uses `DiffUtil` for smooth RecyclerView animations and background thread processing for all database operations.

## 🏗 Architecture & Tech Stack

-   **Language**: Java
-   **Database**: Android Room Persistence Library
-   **UI Pattern**: Components-based architecture (MainActivity, Adapter, DAO, Entity).
-   **Components**: 
    -   `RecyclerView` with `DiffUtil`
    -   `Material Components` (CardView, BottomSheet, FloatingActionButton, TextInputLayout)
    -   `ConstraintLayout` & `CoordinatorLayout`
    -   `Vector Drawables` for crisp icons

## 📂 Project Structure

-   `com.example.mytodo.data`: Contains `Task` entity, `TaskDao` interface, and `TaskDatabase` (Singleton).
-   `com.example.mytodo.adapter`: Contains `TaskAdapter` for binding data to the UI.
-   `com.example.mytodo`: Contains UI logic like `MainActivity` and `AddTaskActivity`.
-   `res/layout`: Contains all XML designs including the custom `item_task.xml` and `bottom_sheet_task_detail.xml`.

## 🛠 Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone git@github.com:YeaminHasan/MyToDo.git
    ```
2.  **Open in Android Studio**:
    -   Import the project.
    -   Wait for Gradle Sync to complete.
3.  **Build and Run**:
    -   Connect an Android device or use an emulator.
    -   Click the **Run** button in Android Studio.

## 📝 Usage

-   **Add Task**: Click the `+` button, enter title/description, select priority and due date.
-   **Mark Complete**: Tap the checkbox. The task will strike through and move to the bottom.
-   **View Details**: Tap on any task to see the full description in a bottom sheet.
-   **Edit**: Click the "Edit Task" button inside the detail view or long-press.
-   **Delete**: Click the trash icon to remove a task.

---
Built with ❤️ for productivity.
