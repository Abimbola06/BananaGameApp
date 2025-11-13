# BananaGameApp
Student: Abimbola Agbeleye
Student ID: 2196980
Unit: Software For Enterprise
Programming Language: Java

Overview

BananaGameApp is a Java application developed for the unit assignment.
It demonstrates four key Computer Science themes:

Version Control

Event-Driven Programming

Interoperability

Virtual Identity

The app connects to the Banana API to retrieve random quiz questions, uses simple authentication for user identity, and reacts to user events such as button clicks.
All progress is tracked and managed with Git and GitHub for version control.


Architecture
+-------------+        +------------------+        +----------------------+
|  GameUI     | --->   |  BananaAPIClient | --->   |  Banana API (External) |
| (Events)    | <---   |  AuthManager     | <---   |  User credentials      |
+-------------+        +------------------+        +------------------------+

Components
+-------------+         +---------------------------------------------------+
| Class	                | Role                                              |
+-------------+         +---------------------------------------------------+
| Main.java	            | Launches the app and initializes components.      |
| GameUI.java	        | Manages the user interface and event triggers.    |
| EventHandler.java	    | Handles event logic between UI and backend.       |
| BananaAPIClient.java	| Connects to the Banana API for quiz data.         |
| AuthManager.java	    | Manages login and user identity.                  |
+-------------+         +--------------------------------------------------+


Version Control

Managed using Git and GitHub.

Commits record development progress and feature updates.

Demonstrates collaboration, modularity, and maintainability.



Event-Driven Programming

Java’s ActionListener interface is used to handle user-triggered events (e.g., button clicks).

Each event triggers logic within the EventHandler and updates the UI.

This reflects the event–response pattern common in modern GUIs.



 Interoperability

Connects to the Banana API at:

https://marcconrad.com/uob/banana/api.php


Demonstrates how Java communicates with an external web API using HTTP requests and JSON parsing.



 Virtual Identity

A simple login mechanism identifies users before gameplay.

In future, cookies or tokens can be added for persistent sessions.

Represents secure virtual identity management in distributed systems.



Technologies Used

Java 25 (Oracle JDK 25.0.1)

VS Code (Java Extension Pack)

Git & GitHub

Banana API (external interoperability component)



How to Run
# Compile
javac src/*.java

# Run
java -cp src Main

🧱 Project Structure
/BananaGameApp
 ├── src/
 │    ├── Main.java
 │    ├── EventHandler.java
 │    ├── AuthManager.java
 │    ├── BananaAPIClient.java
 │    └── GameUI.java
 ├── docs/
 │    ├── architecture-diagram.png
 │    └── presentation-proof.md
 ├── README.md
 └── .gitignore



Acknowledgements

Banana API by Dr. Marc Conrad – University of Bedfordshire

Example code adapted from Comparative Integrated Systems examples
.

Placeholder snippets reviewed and modified with AI assistance (referenced in code comments).

All third-party content is properly acknowledged.



Next Steps

Implement GUI and event-handling (Week 9).

Add API integration (Week 9–10).

Implement login authentication (Week 10).

Produce 10-minute reflective video (Week 11).