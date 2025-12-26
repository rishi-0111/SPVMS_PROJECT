🔐 Vendor Management Module – Vendor Security
📌 Project Overview

This project focuses on building a secure Vendor Management Module using Java and Spring Boot, with special attention to vendor data security and confidentiality.

Vendors often share sensitive information such as bank details, GST numbers, and compliance documents. If these are not handled securely, it can lead to data breaches and compliance risks. This system solves that problem by implementing strong security mechanisms and controlled access.

🎯 Objective

The main goal of this project is to:

Protect sensitive vendor data

Allow only authorized users to access information

Ensure vendors meet compliance requirements

Track vendor activities for audit purposes

🧩 Key Features

Secure vendor registration and login

Role-based access control (Admin, Vendor, Auditor)

JWT-based authentication

Encrypted storage of sensitive data

Compliance document upload and expiry tracking

Vendor risk assessment (Low / Medium / High)

Audit logs for monitoring user actions

🛠️ Technologies Used

Programming Language: Java

Framework: Spring Boot

Security: Spring Security, JWT, BCrypt

Database: MySQL

Frontend: HTML, CSS, JavaScript

🏗️ System Architecture (Simple Flow)

User logs in using credentials

Server validates the user and generates a JWT token

Token is used for all secured API requests

Access is granted based on user role

Vendor data is fetched securely from the database

🔐 Security Implementation

Passwords are encrypted using BCrypt

Sensitive vendor data is encrypted before storage

JWT tokens are used to secure API communication

Role-based access ensures data confidentiality

All critical actions are logged for auditing

📂 Project Structure
vendor-management/
 ├── controller/
 ├── service/
 ├── repository/
 ├── entity/
 ├── security/
 ├── dto/
 ├── exception/
 └── VendorManagementApplication.java

🗄️ Database Tables

users

vendors

roles

vendor_documents

audit_logs

risk_scores
