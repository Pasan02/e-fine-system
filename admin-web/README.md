# Sri Lanka Police Traffic Fine System — Admin Web Portal

The **Admin Web Portal** is a high-fidelity, single-page React application built on Vite. It is designed to provide senior officials of the Sri Lanka Police Department with national oversight and real-time analytical insights into traffic fine collections, regional enforcement levels, and payment trends.

This portal is part of the **E-Fine System** digitalization initiative.

---

## Key Features

- **🔒 JWT Authentication (Mocked):**
  - Secured route guards preventing unauthorized access.
  - Session state persistence in local storage.
  
- **📊 Interactive Analytics Dashboard (`/dashboard`):**
  - High-level KPIs: Total Revenue (LKR), Total Citations Issued, Paid Settlement Rate, Pending/Notice Period Counts, and Expired (Court Reference) Volumes.
  - Regional Collections Bar Chart & Citation Settlement Ratio Doughnut Chart.
  - Real-time Violation Citations stream table.

- **🗺️ District Collections Registry (`/collections`):**
  - Provincial enforcement volume bar chart comparison.
  - Advanced search (Reference Number, Driver Name, License, Vehicle, Location).
  - List filtering by status (Paid, Pending, Expired) and province.
  - Multi-column interactive sorting.
  - **CSV Exporter:** Instantly download the filtered registry data directly to a `.csv` sheet.

- **🎫 Statutory Fine Categories Tariff (`/categories`):**
  - Visual share chart displaying revenue breakdown by category codes (e.g., Speeding, Drunk Driving, Seatbelts).
  - Cards listing code, description, active status, tariff rate, and count of citations issued.

- **📈 Time-Series Trends (`/trends`):**
  - Graphs tracing daily/monthly collection rates and fine sheet issuance volumes.
  - Key trend indicators including Average Daily Revenue and enforcement growth percentage.

---

## Tech Stack & Architecture

- **Core:** React 19 + Vite 8
- **Styling:** Vanilla CSS (Tailored Dark Glassmorphism, smooth animations, responsive flex/grid layouts)
- **Routing:** React Router DOM (v6)
- **Charts:** Chart.js + React ChartJS 2
- **Icons:** Lucide React
- **Security:** Stateless JWT authentication workflow

---

## Getting Started

### Installation

1. Navigate to the `admin-web` directory:
   ```bash
   cd admin-web
   ```
2. Install dependencies:
   ```bash
   npm install
   ```

### Running Locally

To start the Vite development server, run:
   ```bash
   npm run dev
   ```

### Default Credentials (Mock Login)

- **Username:** `admin`
- **Password:** `admin123`

### Build for Production

To compile the application into static assets for production deployment:
   ```bash
   npm run build
   ```
The compiled output will be generated inside the `dist/` directory.
