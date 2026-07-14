# Sri Lanka Police Traffic Fine Payment System

## Overview

The Traffic Fine Payment System is a comprehensive digital solution designed to streamline the process of issuing, paying, and monitoring traffic fines. The system digitizes the traditional paper-based process, enabling drivers to pay fines online or via a mobile app, while providing police officers and senior administrators with real-time updates and analytics.

## System Architecture

The project follows a **Modular Monolith** architecture with Clean Architecture principles for the backend, alongside dedicated frontend clients for different user roles.

The system is composed of the following core components:
1. **Backend REST API** (Java / Spring Boot)
2. **Payment Web SPA** (React + Vite)
3. **Admin Web Portal** (React + Vite)
4. **Android Mobile App** (Flutter)
5. **Database** (PostgreSQL)

## Modules Implemented

As planned in our initial implementation strategy, all core modules have been successfully developed and integrated.

### 1. Backend REST API
Built with Java 17 and Spring Boot 3.x, featuring a layered Clean Architecture structure.
*   **Auth Module**: Secure authentication and authorization using Spring Security and JWT. Roles include ADMIN, OFFICER, and DRIVER.
*   **Fine Module**: Endpoints for issuing fines, verifying fines via reference numbers, and listing fines by officer.
*   **Payment Module**: Handles payment processing, verification, and updates fine statuses.
*   **SMS Module**: Integrates with external SMS gateways to notify officers when a driver successfully pays a fine.
*   **Admin & Export Module**: Aggregates data for national/district summaries and handles data export features.

### 2. Payment Web SPA
A web portal built with React and Vite focused on the motorist experience.
*   Enables drivers to search for fines using their reference number and category code.
*   Provides a streamlined payment form and payment confirmation/receipt pages.
*   Responsive, mobile-first design.

### 3. Admin Web Portal
A dedicated dashboard for senior officials, built with React and Vite.
*   JWT-secured routes.
*   Provides analytical dashboards with metrics, district-wise collection tables, category breakdowns, and time-series trends.

### 4. Mobile Application
An Android application built using Flutter for on-the-go access.
*   Supports fine lookup, detailed fine display, and payment processing.
*   Features offline-friendly UX with robust error handling.

## Technology Stack

*   **Backend**: Java, Spring Boot, Spring Data JPA / Hibernate, Spring Security + JWT
*   **Frontend Web**: React 18, Vite, Tailwind CSS / Vanilla CSS
*   **Mobile**: Flutter 3.x
*   **Database**: PostgreSQL 15+
*   **Resilience & Integration**: Resilience4j (Circuit Breaker), OpenAPI (Swagger)

## Setup and Running Locally

### Backend
1. Ensure PostgreSQL is installed and running.
2. Navigate to the `backend` directory.
3. Configure your database credentials in `application.yml` or `application.properties`.
4. Run the application using Maven: `./mvnw spring-boot:run`

### Web Portals
1. Navigate to the `admin-web` or `payment-web` directory.
2. Install dependencies: `npm install`
3. Start the development server: `npm run dev`

### Mobile App
1. Navigate to the `mobile` directory.
2. Fetch packages: `flutter pub get`
3. Run on a connected device or emulator: `flutter run`

---

*This project was developed for the Software Architecture module, demonstrating architectural decision-making, modular monolith design, clean architecture, and modern full-stack development.*
