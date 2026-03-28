# TA Recruitment System \- README

**Version**: 20260329 Version

**Status**: Compiled normally, full functionality, no errors

**Applicable Roles**: Teaching Assistant \(TA\) / Module Organizer \(MO\) / Administrator \(Admin\)


# 1\. Project Overview

This project is a**Teaching Assistant Recruitment System \(TA Recruitment System\)**, implemented based on JavaFX \+ CSV/JSON persistence technology\. The system supports complete business processes such as **user registration, login, personal profile management, resume upload, position browsing, position application, and application record viewing**\. All bug fixes and function improvements have been completed today, and it can be directly run and fully delivered\.

# 2\. Currently Fully Implemented Functions

## 1\. User Authentication Module \(Login \+ Registration\)

- Supports three roles: Teaching Assistant \(TA\) / Module Organizer \(MO\) / Administrator \(Admin\)

- Username and password login verification function

- New user registration \(username is unique and cannot be duplicated\)

- Error prompt function \(covers scenarios such as empty username/password, incorrect username/password, and username already exists\)

- Ability to return to the login and registration page from any page \(new function\)

## 2\. Core Functions for Teaching Assistants \(TA\) \(All Completed\)

- ✅ TA\-001 Personal Profile Creation
  Fill in student ID, name, email, teachable courses, skill tags, and contact information;
  Automatic verification: unique student ID, standardized email format, and no missing required fields\.

- ✅ TA\-002 Resume Upload and Management
  Supports TXT/PDF/DOC/DOCX formats;
  File size limit: 10MB;
  Supports upload progress bar display;
  Supports resume replacement function;
  Resume upload path is automatically saved to the personal profile\.

- ✅ TA\-003 View List of Available Positions
  Supports pagination display;
  Only displays positions that are in recruitment;
  Sorted in reverse order of release time;
  Supports list refresh function\.

- ✅ TA\-004 Position Application
  Supports duplicate application verification;
  Application status is set to \&\#34;Pending\&\#34; by default;
  Clear prompt is given after successful application\.

- ✅ TA\-005 View Application Records and Status
  Displays all application records of the current user;
  Clearly shows application status: Pending / Approved / Rejected;
  Application details and review comments can be viewed\.

- ✅ Automatic Profile Association \(Most Important Function Today\)
  After a TA logs in, the system automatically checks whether the user has created a personal profile;
  Existing profile → Directly enter the personal details page;
  No profile → Automatically jump to the profile creation page;
  No need to recreate the profile, realizing permanent binding between the account and the profile\.

## 3\. Functions for Module Organizers \(MO\) \(Basic Interface\)

- MO console page

- TA position release function

- Applicant information viewing function

- TA application review function

- Function to return to the role selection page

## 4\. Functions for Administrators \(Admin\) \(Basic Interface\)

- TA workload viewing function

- System management operation function

- Function to return to the role selection page

# 3\. All Modifications and New Functions Completed Today \(2026\-03\-29\)

**Core Focus**: Focus on TA core business scenarios, complete 4 key functions and optimize related experiences, details as follows:

## 1\. Core Function Implementation \(TA Core Needs\)

- \(Key 1\) TA can find available jobs: Develop JobListView, support paginated display of recruiting positions \(sorted by release time\), with \&\#34;View Details\&\#34; and \&\#34;Refresh List\&\#34; buttons\.

- \(Key 2\) TA can apply for jobs: Develop JobDetailView with \&\#34;Apply for This Position\&\#34; button, support duplicate application verification, generate unique application ID, and persist records to CSV\.

- \(Key 3\) TA can check application status: Develop MyApplicationView to display TA\&\#39;s applications \(sorted by application time\), with \&\#34;View Details\&\#34; and \&\#34;Refresh Status\&\#34; buttons\.

- \(Key 4\) Automatic profile association upon login: TA logs in to automatically query the profile \(existing → enter details page; non\-existing → jump to creation page\), realizing seamless binding of account and profile\.

## 2\. Interface and Navigation Optimization

- Restore and optimize the login/registration interface, retain 6 function buttons, and add clear prompts\.

- Add \&\#34;Return to Login and Registration Page\&\#34; button in ProfileDetailView to support account switching\.

- Optimize page jump logic to realize closed\-loop navigation without dead pages\.

## 3\. Data Persistence Improvement

- Optimize CSV \+ JSON dual storage to ensure data persistence \(no data loss after program closure\)\.

- Improve ApplicationRepository to support saving and querying of application records\.

# 4\. System Operation Effect

1. Start the program → Enter the login/registration interface \(supports registration/login, three roles optional\)\.

2. TA registration/login → System automatically queries the profile:
   \- Existing profile → Directly enter the personal details page \(upload resume, view positions, return to login\);
   \- No profile → Enter the profile creation page, bind account automatically after creation\.

3. TA clicks \&\#34;View Available Positions\&\#34; → Browse paginated positions and view details\.

4. TA clicks \&\#34;Apply for This Position\&\#34; → System verifies duplicate applications, saves records after success\.

5. TA clicks \&\#34;My Application Records\&\#34; → View all applications, refresh status and check details\.

6. No errors throughout, natural flow, complete experience\.

# 5\. Operation Method

```java
Main Class to Run: HelloFxApp.java
```

Default Test Accounts \(Can be directly logged in for testing\):

- TA: ta001 / 123456

- MO: mo001 / 123456

- Admin: admin001 / 123456

# 6\. File Structure Description

```plaintext
controller/        Controller (controls interface interaction logic)
├─ HelloController (profile creation and editing logic)
└─ ProfileController (resume upload logic)

service/           Business Logic Layer (core function implementation)
├─ ApplicantService (TA profile-related business)
├─ AuthService (login and registration verification business)
└─ JobService (position and application-related business)

repository/        Data Access Layer (data persistence)
├─ ApplicantCsvRepository (TA profile CSV storage)
├─ ApplicantJsonRepository (TA profile JSON storage)
├─ ApplicationRepository (application record storage)
├─ JobRepository (position data storage)
└─ UserRepository (user account storage)

model/             Data Model Layer (encapsulates data)
├─ Applicant (TA profile model)
├─ Application (application record model)
├─ Job (position model)
└─ User (user account model)

view/              View Layer (displays interface)
├─ RoleSelectView (login and registration interface, optimized today)
├─ HelloView (profile creation interface)
├─ ProfileDetailView (personal details interface, added return button today)
├─ JobListView (position list interface, focus development today)
├─ JobDetailView (position details interface, focus development today)
├─ MyApplicationView (application records interface, focus development today)
├─ TeacherView (MO console interface)
└─ AdminView (Admin console interface)

util/              Tool Class
└─ MD5Util (password encryption tool)

HelloFxApp.java    Program Entry (startup class)
```

**Developer**: Luo Zhiyi，Li Jingyu


