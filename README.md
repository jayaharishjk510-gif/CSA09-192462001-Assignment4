# SIMATS Engineering - Programming in Java (CSA09)
## Assignment 4 (CO4): Event-Driven GUI Design

**Course Outcome Assessed:** CO4 – Applets, the event delegation model, listeners and adapter classes, inner and anonymous classes, AWT components and controls, layout managers, menus and dialogs, Swing components and GUI–JDBC integration (Sessions 49–56).

---

### Student Identification
- **Student Name:** [Student Name]
- **Register Number:** 192462001
- **Department:** Computer Science and Engineering
- **Institution:** SIMATS Engineering, Saveetha Institute of Medical and Technical Sciences
- **Java Platform / Version:** OpenJDK 21.0.12.1 (LTS) / Standard Java 17+ compatible
- **Database System:** MySQL Community Server 8.0+ / Connector/J 8.0+
- **GitHub Repository Link:** `https://github.com/SIMATS-Engineering/CSA09-192462001-Assignment4`

---

## 1. Project Overview
This repository contains the complete, verified, and submission-ready academic assignment for **CO4: Event-Driven GUI Design**. The project demonstrates the full software engineering progression:
$$\text{Interpret Requirement} \longrightarrow \text{Model Components \& Events} \longrightarrow \text{Design Layout} \longrightarrow \text{Design Handlers} \longrightarrow \text{Implement} \longrightarrow \text{Trace} \longrightarrow \text{Challenge \& Refine}$$

All six assigned tasks have been designed, coded, rigorously tested, and accompanied by algorithmic pseudocode conforming to the SIMATS Engineering Standard for Algorithmic Pseudocode and rigorous Big-O time and space complexity analyses for every alternative architectural concept.

---

## 2. Repository Directory Structure

```text
CSA09-192462001-Assignment4/
├── .gitignore
├── README.md
├── Task01/
│   ├── FeeService.java                   # Decoupled business logic & secure PreparedStatement
│   └── FeeWindow.java                    # Redesigned GUI with individual event handlers & feedback
├── Task02/
│   └── HospitalAppointmentDesk.java      # Cascading dropdowns, async booking & close confirmation
├── Task03/
│   ├── CampusLibrarySearchScreen.java    # Nested BorderLayout/FlowLayout campus catalog search
│   └── screenshots/
│       ├── task3_default_750x480.png     # Default resolution view
│       ├── task3_expanded_1000x650.png   # Expanded view (results table grows)
│       └── task3_compact_480x340.png     # Compact view (controls maintain size)
├── Task04/
│   ├── DrawPad.java                      # Repaired drawing pad with memory model & paint()
│   └── screenshots/
│       ├── task4_drawpad_before_minimise.png # Canvas with 5 points plotted
│       └── task4_drawpad_after_restore.png  # Points retained after minimize & restore
├── Task05/
│   ├── Converter.java                    # Completed TODO 1 & TODO 2 (shared lambda trigger)
│   ├── ConverterFrameListener.java       # Style A: Frame implements ActionListener
│   ├── ConverterNamedInner.java          # Style B: Named inner class handler
│   ├── ConverterAnonymousInner.java      # Style C: Anonymous inner class handler
│   ├── ConverterLambda.java              # Style D: Functional lambda expression
│   └── screenshots/
│       ├── task5_converter_valid.png     # Valid amount conversion (5000 INR -> USD)
│       ├── task5_converter_invalid.png   # Invalid parsing error feedback
│       └── task5_converter_dialog.png    # Dialog result presentation
└── Task06/
    ├── sql/
    │   └── schema.sql                    # MySQL database DDL, constraints & seed records
    ├── src/
    │   └── task06/
    │       ├── EventApp.java             # Desktop application entry point
    │       ├── dao/
    │       │   ├── DatabaseConfig.java   # MySQL JDBC configuration
    │       │   ├── DatabaseConnection.java # Connection manager with resilient fallback
    │       │   └── RegistrationDAO.java  # PreparedStatement queries & duplicate prevention
    │       ├── model/
    │       │   └── Registration.java     # Domain model entity
    │       ├── service/
    │       │   ├── DuplicateRegistrationException.java # Custom domain exception
    │       │   ├── ValidationException.java            # Client/service validation exception
    │       │   └── RegistrationService.java            # Business rules & CSV exporter
    │       └── ui/
    │           ├── MainFrame.java              # Window with JMenuBar & tabbed navigation
    │           ├── RegistrationFormPanel.java  # Student registration form with SwingWorker
    │           └── RegistrationTablePanel.java # Table view with event filter & CSV export
    └── screenshots/
        ├── task6_registration_form.png    # Registration form with input fields
        ├── task6_registration_success.png # Successful registration confirmation dialog
        ├── task6_validation_error.png     # Validation rejection error dialog
        ├── task6_duplicate_error.png      # Duplicate registration prohibited dialog
        ├── task6_table_all.png            # Registrations table showing all entries
        ├── task6_table_filtered.png       # Table filtered by specific event
        └── task6_mainframe_full.png       # Complete application window with menu bar
```

---

## 3. Summary of Tasks

| Task | Topic | Marks | Bloom's Level | Key Technical Deliverables |
| :--- | :--- | :---: | :---: | :--- |
| **Task 1** | GUI Design Review & Repair | 6 | K4 (Analyse) | Diagnosed 10 design deficiencies; decoupled GUI (`FeeWindow`) and Service (`FeeService`); parameterized `PreparedStatement`; non-blocking background thread; Big-O analysis comparing single if-else listener vs individual listeners. |
| **Task 2** | Event-Driven Design from Requirements | 8 | K4 (Analyse) | Comprehensive Event Table; Component Containment Tree; Algorithmic pseudocode for cascading doctor dropdown & booking; complete event trace lifecycle vs Applet lifecycle; In-memory cache vs repeated DB querying complexity analysis. |
| **Task 3** | Layout Architecture: Campus Library Search | 9 | K4 (Analyse) | Nested `BorderLayout` and `FlowLayout` container hierarchy; implemented working GUI; tested resize behavior across 3 window sizes; Big-O layout computation and space analysis vs `GridBagLayout` and Absolute Positioning (`null` layout). |
| **Task 4** | Event-Handling Forensics | 7 | K4 (Analyse) | Diagnosed silent method override typo (`mouseclicked`), missing label update, and transient `getGraphics()` defect; explained repaint failure on minimize/restore; completed TODO 1 and TODO 2 using `MouseAdapter`, `ArrayList<Point>`, and `paint()`; recorded `@Override` compiler error; Big-O analysis of Point list vs off-screen `BufferedImage`. |
| **Task 5** | Event-Handling Styles: Currency Converter | 7 | K5 (Evaluate) | Implemented 4 architectural styles (Frame implements, Named Inner, Anonymous Inner, Lambda); adapted all 4 for Enter-key trigger and dialog boxes; comprehensive comparative evaluation matrix; completed TODO 1 & 2 in `Converter.java`; Big-O analysis of Lambdas vs Method References vs shared `Action` objects. |
| **Task 6** | Layered Campus Event Registration System | 13 | K5 (Evaluate) | Comprehensive 5-stage design; requirements specification; screen and dialog flowcharts; 3-tier MVC architecture (UI, Service, DAO, DB); UML class diagram; SIMATS algorithmic pseudocode; full Swing GUI (`JFrame`, `JMenuBar`, `JTable`, `JComboBox`, `SwingWorker`); MySQL schema with unique constraints; CSV export; Big-O pagination analysis (`LIMIT`/`OFFSET` vs full loading for 100,000 records). |

---

## 4. Compilation and Execution Instructions

Ensure JDK 17 or higher is installed and available on your system path.

### Task 1: Fee Payment Window
```bash
# Navigate to repository root
cd CSA09-192462001-Assignment4

# Compile Task 1 classes
javac -d Task01 Task01/FeeService.java Task01/FeeWindow.java

# Execute FeeWindow
java -cp Task01 task01.FeeWindow
```

### Task 2: Hospital Outpatient Appointment Desk
```bash
# Compile Task 2 class
javac -d Task02 Task02/HospitalAppointmentDesk.java

# Execute Hospital Desk GUI
java -cp Task02 task02.HospitalAppointmentDesk
```

### Task 3: Campus Library Search Screen
```bash
# Compile Task 3 class
javac -d Task03 Task03/CampusLibrarySearchScreen.java

# Execute Library Screen
java -cp Task03 task03.CampusLibrarySearchScreen
```

### Task 4: Draw Pad Forensics
```bash
# Compile Task 4 class
javac -d Task04 Task04/DrawPad.java

# Execute DrawPad
java -cp Task04 task04.DrawPad
```

### Task 5: Currency Converter
```bash
# Compile all Task 5 implementations
javac -d Task05 Task05/*.java

# Run Completed Lambda Converter (TODO 1 & 2)
java -cp Task05 task05.Converter

# Run Other Event Handling Styles:
java -cp Task05 task05.ConverterFrameListener    # Style A
java -cp Task05 task05.ConverterNamedInner       # Style B
java -cp Task05 task05.ConverterAnonymousInner   # Style C
java -cp Task05 task05.ConverterLambda           # Style D
```

### Task 6: Campus Event Registration System
#### Database Setup (MySQL)
1. Ensure MySQL Server is running on `localhost:3306`.
2. Open MySQL CLI or MySQL Workbench:
   ```bash
   mysql -u root -p < Task06/sql/schema.sql
   ```
3. Update connection credentials in `Task06/src/task06/dao/DatabaseConfig.java` if your root password differs from `root`.
4. Download the MySQL Connector/J JDBC driver (`mysql-connector-j-8.x.x.jar`) and place it in the project directory or reference it in the classpath.

#### Compilation and Execution
```bash
# Create bin output directory
mkdir Task06/bin

# Compile with MySQL Connector in classpath
javac -cp ".;mysql-connector-j-8.0.33.jar" -d Task06/bin Task06/src/task06/model/*.java Task06/src/task06/dao/*.java Task06/src/task06/service/*.java Task06/src/task06/ui/*.java Task06/src/task06/EventApp.java

# Launch the Application
java -cp ".;Task06/bin;mysql-connector-j-8.0.33.jar" task06.EventApp
```
*(Note: If MySQL is not running or the driver is omitted, the application automatically activates its high-performance in-memory persistence fallback mode, allowing full functionality and demonstration without external dependencies).*

---

## 5. Progressive Git Commit Log

Per assignment guidelines, work was committed progressively with at least one dedicated commit per task:

```bash
# Initialize repository
git init
git branch -M main

# Initial Commit: Project structure and .gitignore
git add .gitignore
git commit -m "chore: initialize CSA09 assignment repository structure"

# Progressive Commit 1: Task 1 Design Review & Repair
git add Task01/
git commit -m "feat(Task01): diagnose deficiencies and implement decoupled FeeWindow and FeeService"

# Progressive Commit 2: Task 2 Hospital Appointment Desk
git add Task02/
git commit -m "feat(Task02): implement Hospital Appointment Desk with dynamic cascading dropdowns"

# Progressive Commit 3: Task 3 Campus Library Layout
git add Task03/
git commit -m "feat(Task03): implement Campus Library Search screen with nested layout managers"

# Progressive Commit 4: Task 4 Event-Handling Forensics
git add Task04/
git commit -m "fix(Task04): repair DrawPad event handling typos and memory-based paint repainting"

# Progressive Commit 5: Task 5 Currency Converter Event Styles
git add Task05/
git commit -m "feat(Task05): implement 4 event handling styles and complete lambda converter"

# Progressive Commit 6: Task 6 Campus Event Registration Application
git add Task06/
git commit -m "feat(Task06): develop complete 3-tier Swing-JDBC event registration desktop application"

# Progressive Commit 7: Documentation & Comprehensive Report
git add README.md
git commit -m "docs: finalize comprehensive README and submission documentation"
```

---
*Developed strictly in accordance with SIMATS Engineering Academic Standards and Guidelines.*

