# Parallel Development Guide: E-Fine System

When working under a tight deadline, parallel development is essential. However, working in silos can lead to massive integration headaches later. This guide outlines how the 6 team members can work simultaneously on their respective branches without blocking each other.

## 1. Branch Strategy & Isolation

Every team member has their own branch (e.g., `feature/member3-mobile-app`). 
- **Rule of Thumb:** NEVER push directly to `main`.
- **Stay Updated:** While you work on your branch, other members might merge their completed components to `main`. You should periodically pull from `main` into your branch to avoid massive merge conflicts at the end:
  ```bash
  git fetch origin
  git merge origin/main
  ```
- **Scope Restriction:** **Only touch the files in your assigned module.**
  - If you are Member 4 (Payment Web), you should strictly only be editing files inside the `payment-web/` directory.
  - If you need a change in the backend, **do not edit the backend files on your branch**. Ask the backend developer to implement the change on their branch and merge it to `main`.

## 2. Unblocking the Frontend: Mocking Data

> [!TIP]
> **You are completely right!** The frontend teams (Mobile, Payment Web, Admin Web) should NOT wait for the backend APIs to be fully developed. Instead, they should rely on hardcoded/mocked data.

### Option A: Hardcoded Mock Objects (Recommended for Speed)
The easiest way to unblock the frontend is to create a fake API service or hardcode the expected JSON responses directly in the frontend components.

**Example (Payment Web - React):**
Instead of fetching fines from the actual backend, create a fake service returning promises.

```javascript
// src/services/mockFineService.js
export const fetchFineDetails = async (referenceNo) => {
  // Simulate network delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  // Return hardcoded mock data matching the expected Backend DTO
  return {
    referenceNumber: referenceNo,
    driverName: "John Doe",
    vehicleNumber: "CBA-1234",
    amount: 5000.00,
    status: "PENDING",
    issuedAt: "2026-06-08T10:30:00Z"
  };
};
```

When the real backend is ready, you only need to swap `mockFineService.js` with the real `axios` API calls.

### Option B: Local Mock Server (JSON Server)
If you want to simulate real HTTP requests without touching the React/Flutter code, you can use a tool like `json-server`.
1. Create a `db.json` file with mock responses.
2. Run `npx json-server --watch db.json --port 8080`.
3. Point your frontend API URLs to `http://localhost:8080`.

## 3. The API Contract: The Single Source of Truth

For the frontend mocking strategy to work, the Backend and Frontend developers **must agree on the API Contract** (the JSON shape of requests and responses) on **Day 1**.

> [!IMPORTANT]  
> If the frontend mocks data shaped like `{ "id": 1, "car": "CBA-1234" }` but the backend eventually builds `{ "fineId": 1, "vehicleNumber": "CBA-1234" }`, the frontend will completely break during integration.

**Action Item:**
Backend developers (Members 1 & 2) should write a quick document or Swagger/OpenAPI spec defining the exact JSON format for all endpoints *before* they even start coding the Java logic. The frontend developers must mock their data exactly according to this spec.

## 4. Integration Strategy (The "Big Merge")

Do not wait until Day 5 to integrate everything!
1. As soon as Backend Member 1 finishes the `/api/fines` endpoint, they should create a Pull Request to `main`.
2. Once merged, Frontend members can pull `main` into their branches and swap their mock data for the real `localhost:8080/api/fines` endpoint.
3. Test integrations incrementally rather than all at once on the final day.
