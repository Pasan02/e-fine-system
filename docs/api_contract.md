# E-Fine System — API Contract
**Owner:** Member 1 (Backend Core: Fine & Payment Modules)
**Base URL:** `http://localhost:8080`
**Auth:** JWT Bearer token (where required) — obtained from `POST /api/auth/login`

> [!IMPORTANT]
> **Frontend Teams (Members 3, 4, 5):** Mock your data using **exactly** these JSON shapes.
> Field names, types, and nesting must match precisely or integration will break.
> See `docs/parallel_development_guide.md` Section 3 for the mock service pattern.

---

## Common Types

### `FineStatus`
```
"PENDING" | "PAID" | "EXPIRED"
```

### `PaymentMethod`
```
"CARD" | "MOBILE_WALLET"
```

### `PaymentChannel`
```
"MOBILE_APP" | "WEB_PORTAL"
```

### Error Response (all endpoints)
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Fine not found with reference number: TF-2026-WP-99999",
  "timestamp": "2026-06-10T18:30:00"
}
```

---

## Authentication Endpoints
> *(Implemented by Member 6 — listed here for reference)*

### `POST /api/auth/login`
**Auth:** Public

**Request:**
```json
{
  "username": "officer1",
  "password": "password123"
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "OFFICER"
}
```

---

## Fine Endpoints

### `GET /api/fines/{referenceNumber}`
**Auth:** Public — no JWT needed
**Description:** Look up a fine by the reference number printed on the fine sheet.

**Example Request:**
```
GET /api/fines/TF-2026-WP-00001
```

**Response `200 OK` — `FineDTO`:**
```json
{
  "id": 1,
  "referenceNumber": "TF-2026-WP-00001",
  "officerId": 5,
  "officerName": "P. K. Silva",
  "officerDistrict": "WP",
  "categoryCode": "SPD01",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amount": 1500.00,
  "driverLicenseNo": "B1234567",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "district": "WP",
  "location": "Colombo 03, Marine Drive",
  "status": "PENDING",
  "issuedAt": "2026-06-10T14:22:00"
}
```

**Error `404`:** Fine not found.

---

### `GET /api/fines/verify?referenceNumber=...&categoryCode=...`
**Auth:** Public — no JWT needed
**Description:** Verifies a fine using both identifiers before payment. Returns fine details
including the exact amount the driver must pay. Only works for `PENDING` fines.

**Example Request:**
```
GET /api/fines/verify?referenceNumber=TF-2026-WP-00001&categoryCode=SPD01
```

**Response `200 OK` — `FineDTO` (same shape as above):**
```json
{
  "id": 1,
  "referenceNumber": "TF-2026-WP-00001",
  "officerId": 5,
  "officerName": "P. K. Silva",
  "officerDistrict": "WP",
  "categoryCode": "SPD01",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amount": 1500.00,
  "driverLicenseNo": "B1234567",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "district": "WP",
  "location": "Colombo 03, Marine Drive",
  "status": "PENDING",
  "issuedAt": "2026-06-10T14:22:00"
}
```

**Error `404`:** No fine found matching both identifiers.
**Error `400`:** Fine is already `PAID` or `EXPIRED`.

---

### `POST /api/fines`
**Auth:** 🔒 JWT required — `OFFICER` role only
**Description:** Issues a new traffic fine to a driver. The officer ID is read from the JWT token automatically — do not send it in the request body.

**Request Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body — `CreateFineRequest`:**
```json
{
  "categoryCode": "SPD01",
  "driverLicenseNo": "B1234567",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "district": "WP",
  "location": "Colombo 03, Marine Drive"
}
```

**Field Rules:**
| Field | Required | Max Length | Notes |
|-------|----------|------------|-------|
| `categoryCode` | ✅ | 20 | Must match an active category in the DB |
| `driverLicenseNo` | ✅ | 20 | |
| `driverName` | ✅ | — | |
| `vehicleNumber` | ✅ | 15 | Pattern: `^[A-Z0-9\-]{2,15}$` |
| `district` | ✅ | 50 | Used in reference number generation |
| `location` | ❌ | — | Free text, optional |

**Response `201 Created` — `FineDTO`:**
```json
{
  "id": 42,
  "referenceNumber": "TF-2026-WP-00042",
  "officerId": 5,
  "officerName": "P. K. Silva",
  "officerDistrict": "WP",
  "categoryCode": "SPD01",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amount": 1500.00,
  "driverLicenseNo": "B1234567",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "district": "WP",
  "location": "Colombo 03, Marine Drive",
  "status": "PENDING",
  "issuedAt": "2026-06-10T15:05:00"
}
```

**Error `400`:** Invalid/inactive `categoryCode`, validation failure.
**Error `401`:** Missing or invalid JWT.
**Error `403`:** Authenticated user is not an OFFICER.

---

### `GET /api/fines/officer/{officerId}`
**Auth:** 🔒 JWT required — `OFFICER` or `ADMIN` role
**Description:** Returns all fines issued by a specific officer.

**Example Request:**
```
GET /api/fines/officer/5
Authorization: Bearer <jwt_token>
```

**Response `200 OK` — `List<FineDTO>`:**
```json
[
  {
    "id": 1,
    "referenceNumber": "TF-2026-WP-00001",
    "officerId": 5,
    "officerName": "P. K. Silva",
    "officerDistrict": "WP",
    "categoryCode": "SPD01",
    "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
    "amount": 1500.00,
    "driverLicenseNo": "B1234567",
    "driverName": "A. B. Perera",
    "vehicleNumber": "CAR-1234",
    "district": "WP",
    "location": "Colombo 03, Marine Drive",
    "status": "PAID",
    "issuedAt": "2026-06-10T14:22:00"
  },
  {
    "id": 2,
    "referenceNumber": "TF-2026-WP-00002",
    "officerId": 5,
    "officerName": "P. K. Silva",
    "officerDistrict": "WP",
    "categoryCode": "SIG01",
    "categoryDescription": "Jumping a red traffic signal",
    "amount": 3000.00,
    "driverLicenseNo": "C9876543",
    "driverName": "D. S. Fernando",
    "vehicleNumber": "BUS-5678",
    "district": "WP",
    "location": "Nugegoda Junction",
    "status": "PENDING",
    "issuedAt": "2026-06-10T16:10:00"
  }
]
```

**Response `200 OK` (no fines):** `[]`
**Error `404`:** Officer not found.

---

## Payment Endpoints

### `POST /api/payments`
**Auth:** Public — no JWT needed
**Description:** Processes payment for a fine. The dual-key (referenceNumber + categoryCode)
acts as the driver's identity verification. Amount must exactly match the fine category's fixed amount.

**Request Body — `PaymentRequest`:**
```json
{
  "referenceNumber": "TF-2026-WP-00001",
  "categoryCode": "SPD01",
  "amount": 1500.00,
  "paymentMethod": "CARD",
  "paymentChannel": "WEB_PORTAL",
  "transactionRef": "PAY-XYZ-789012"
}
```

**Field Rules:**
| Field | Required | Notes |
|-------|----------|-------|
| `referenceNumber` | ✅ | From physical fine sheet |
| `categoryCode` | ✅ | From physical fine sheet |
| `amount` | ✅ | Must exactly match the fine category's fixed amount |
| `paymentMethod` | ✅ | `"CARD"` or `"MOBILE_WALLET"` |
| `paymentChannel` | ✅ | `"MOBILE_APP"` or `"WEB_PORTAL"` |
| `transactionRef` | ✅ | Reference from your payment gateway / mock gateway |

> **For Members 3 & 4 (Mobile/Web):** Generate a random mock `transactionRef` like `"MOCK-TXN-" + Date.now()` when mocking. In production, this comes from the payment gateway callback.

**Response `201 Created` — `PaymentDTO`:**
```json
{
  "id": 10,
  "fineId": 1,
  "referenceNumber": "TF-2026-WP-00001",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amountPaid": 1500.00,
  "paymentMethod": "CARD",
  "paymentChannel": "WEB_PORTAL",
  "transactionRef": "PAY-XYZ-789012",
  "paidAt": "2026-06-10T17:45:30"
}
```

**Errors:**
| Code | Reason |
|------|--------|
| `404` | No fine found matching referenceNumber + categoryCode |
| `400` | Fine is already `PAID` |
| `400` | Fine is `EXPIRED` |
| `400` | `amount` does not match the category's fixed price |
| `400` | A payment already exists for this fine (duplicate) |

---

### `GET /api/payments/{id}`
**Auth:** 🔒 JWT required (any authenticated user)
**Description:** Retrieves a payment receipt by the payment's own ID.

**Example Request:**
```
GET /api/payments/10
Authorization: Bearer <jwt_token>
```

**Response `200 OK` — `PaymentDTO`:**
```json
{
  "id": 10,
  "fineId": 1,
  "referenceNumber": "TF-2026-WP-00001",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amountPaid": 1500.00,
  "paymentMethod": "CARD",
  "paymentChannel": "WEB_PORTAL",
  "transactionRef": "PAY-XYZ-789012",
  "paidAt": "2026-06-10T17:45:30"
}
```

**Error `404`:** Payment not found.

---

### `GET /api/payments/fine/{fineId}`
**Auth:** 🔒 JWT required (any authenticated user)
**Description:** Retrieves the payment associated with a specific fine (by fine DB ID).
Useful for officers to confirm a fine has been paid before releasing a licence.

**Example Request:**
```
GET /api/payments/fine/1
Authorization: Bearer <jwt_token>
```

**Response `200 OK` — `PaymentDTO` (same shape as above):**
```json
{
  "id": 10,
  "fineId": 1,
  "referenceNumber": "TF-2026-WP-00001",
  "driverName": "A. B. Perera",
  "vehicleNumber": "CAR-1234",
  "categoryDescription": "Exceeding speed limit in urban area (< 20 km/h over)",
  "amountPaid": 1500.00,
  "paymentMethod": "CARD",
  "paymentChannel": "WEB_PORTAL",
  "transactionRef": "PAY-XYZ-789012",
  "paidAt": "2026-06-10T17:45:30"
}
```

**Error `404`:** Fine not found, or fine has not been paid yet.

---

## Available Fine Category Codes (Seeded)

Frontend teams can use any of these `categoryCode` values in their mocks:

| Code | Description | Amount (LKR) |
|------|-------------|-------------|
| `SPD01` | Exceeding speed limit in urban area (< 20 km/h over) | 1,500 |
| `SPD02` | Exceeding speed limit in urban area (20-40 km/h over) | 3,000 |
| `SPD03` | Exceeding speed limit on highway (> 40 km/h over) | 6,000 |
| `SIG01` | Jumping a red traffic signal | 3,000 |
| `SIG02` | Ignoring a stop sign | 1,500 |
| `SFT01` | Driver not wearing seatbelt | 1,000 |
| `SFT03` | Motorcyclist not wearing helmet | 1,500 |
| `MOB01` | Using mobile phone while driving | 3,000 |
| `LIC01` | Driving without a valid driving license | 5,000 |
| `LIC03` | Vehicle without valid revenue license | 5,000 |
| `PRK01` | Parking in a no-parking zone | 1,000 |
| `ALC01` | Driving under the influence of alcohol | 25,000 |

---

## Frontend Mock Service Template

Copy this into your frontend project to mock Member 1's API.
Swap the mock service for real Axios calls once the backend is running.

### React (Members 4 & 5)

```javascript
// src/services/mockFineService.js

const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

export const verifyFine = async (referenceNumber, categoryCode) => {
  await delay(500); // Simulate network delay
  return {
    id: 1,
    referenceNumber: referenceNumber,
    officerId: 5,
    officerName: "P. K. Silva",
    officerDistrict: "WP",
    categoryCode: categoryCode,
    categoryDescription: "Exceeding speed limit in urban area (< 20 km/h over)",
    amount: 1500.00,
    driverLicenseNo: "B1234567",
    driverName: "A. B. Perera",
    vehicleNumber: "CAR-1234",
    district: "WP",
    location: "Colombo 03, Marine Drive",
    status: "PENDING",
    issuedAt: "2026-06-10T14:22:00"
  };
};

export const processPayment = async (paymentRequest) => {
  await delay(800);
  return {
    id: 10,
    fineId: 1,
    referenceNumber: paymentRequest.referenceNumber,
    driverName: "A. B. Perera",
    vehicleNumber: "CAR-1234",
    categoryDescription: "Exceeding speed limit in urban area (< 20 km/h over)",
    amountPaid: paymentRequest.amount,
    paymentMethod: paymentRequest.paymentMethod,
    paymentChannel: paymentRequest.paymentChannel,
    transactionRef: `MOCK-TXN-${Date.now()}`,
    paidAt: new Date().toISOString()
  };
};
```

### Flutter/Dart (Member 3)

```dart
// lib/services/mock_fine_service.dart

class MockFineService {
  Future<Map<String, dynamic>> verifyFine(
      String referenceNumber, String categoryCode) async {
    await Future.delayed(const Duration(milliseconds: 500));
    return {
      'id': 1,
      'referenceNumber': referenceNumber,
      'officerId': 5,
      'officerName': 'P. K. Silva',
      'officerDistrict': 'WP',
      'categoryCode': categoryCode,
      'categoryDescription': 'Exceeding speed limit in urban area (< 20 km/h over)',
      'amount': 1500.00,
      'driverLicenseNo': 'B1234567',
      'driverName': 'A. B. Perera',
      'vehicleNumber': 'CAR-1234',
      'district': 'WP',
      'location': 'Colombo 03, Marine Drive',
      'status': 'PENDING',
      'issuedAt': '2026-06-10T14:22:00',
    };
  }

  Future<Map<String, dynamic>> processPayment(
      Map<String, dynamic> paymentRequest) async {
    await Future.delayed(const Duration(milliseconds: 800));
    return {
      'id': 10,
      'fineId': 1,
      'referenceNumber': paymentRequest['referenceNumber'],
      'driverName': 'A. B. Perera',
      'vehicleNumber': 'CAR-1234',
      'categoryDescription': 'Exceeding speed limit in urban area',
      'amountPaid': paymentRequest['amount'],
      'paymentMethod': paymentRequest['paymentMethod'],
      'paymentChannel': paymentRequest['paymentChannel'],
      'transactionRef': 'MOCK-TXN-${DateTime.now().millisecondsSinceEpoch}',
      'paidAt': DateTime.now().toIso8601String(),
    };
  }
}
```

---

## Integration Checklist

When the backend is ready and you want to swap mocks for real API calls:

- [ ] Replace `mockFineService` with `axios.get('/api/fines/verify?...')`
- [ ] Replace mock payment with `axios.post('/api/payments', { ... })`
- [ ] Store JWT from login response and attach as `Authorization: Bearer <token>`
- [ ] Test with the reference number format: `TF-YYYY-DISTRICT-NNNNN`
- [ ] Use category codes from the table above for testing
