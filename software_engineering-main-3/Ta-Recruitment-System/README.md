# TA Recruitment System
TA Recruitment System for International School of BUPT - Software Engineering Course Project

---

## I. ✅ Fully Implemented User Stories

### 1. TA-001 Create Personal TA Application Profile (100% Complete)
Fulfills all 5 acceptance criteria and requirements:
- **Fixed Input Fields**: Name, Student ID, Email, Courses Available to Teach, Skill Tags, Contact Number, Password
- **Data Validation**: Non-null validation for Student ID / Email; Email must contain `@`; clear red error prompts
- **Unique ID Generation**: Automatically generate a unique TA ID with timestamp, bound to profile information and saved
- **Profile Editing**: Support editing and saving profile information; synchronize updates to local storage
- **Encrypted Password Storage**: Password is required and encrypted with MD5 & salt (no plaintext storage)
- **Skill Tags**: Pre-set multiple-choice options aligned with BUPT International School recruitment needs: Java / English / Teaching / Python / Office

### 2. TA-002 Upload Personal Resume & Associate with Profile (100% Complete)
Supports all 6 resume management requirements:
- **Supported Formats**: TXT / PDF / DOC / DOCX
- **File Size Limit**: Single file ≤ 10MB
- **Upload Status**: Display real-time status: Uploading / Success / Failed
- **Progress Bar**: Show real-time upload progress
- **File Management**: Display file name; support replacing uploaded resume
- **Associated Storage**: Resume file path is bound to the applicant profile and saved permanently

---

## II. ✅ Major UI & Interaction Upgrades
- After successful profile creation → **auto jump to profile detail page**
- New page displays **all submitted personal information**
- New page includes **built-in resume upload panel** (integrated workflow)
- Retain profile editing function for information modification at any time

---

## III. ✅ Data Storage Architecture
All data is **persisted locally and will not be lost after restarting the program**:
- `applicants.csv` → Tabular structured storage
- `applicants.json` → JSON format storage
- `resumes/` folder → Stores resume files
- New field supported: `resumePath` (resume file path)

---

## IV. ✅ Issues Fixed
- Cannot find symbol `HBox`
- CSV array index out of bounds (incompatible old data)
- JSON field mismatch error (id → taId)
- Empty file parsing failure
- Incorrect JavaFX package import
- Null pointer exception during page navigation

---

## V. ✅ Full Features
- Complete profile creation form
- Full form validation
- Auto-generated unique TA ID
- Encrypted password storage
- Profile information editing
- Auto jump to detail page after successful creation
- Multi-format resume upload
- Upload progress bar & status prompts
- File size restriction
- Permanent local data storage

---

## VI. 🚀 How to Run
1. Open the project in IntelliJ IDEA
2. Use Maven to build the project
3. In the Maven panel, navigate to: `Plugins → javafx → javafx:run`
4. Double-click `javafx:run` to start the application
5. Create your profile → auto redirect to the detail page
6. Upload and manage your resume

---

## VII. Tech Stack
- Java 21
- JavaFX (GUI)
- MVC Architecture
- Maven (Dependency Management)
- OpenCSV & Jackson (Data Persistence)
- MD5 (Password Encryption)
- JUnit 5 (Unit Testing)

---

## VIII. Developer
Li Jingyu  Luo Zhiyi
