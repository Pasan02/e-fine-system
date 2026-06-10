// src/services/mockApiService.js

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

// Mock Seed Data
const MOCK_FINES = [
  {
    id: 1,
    referenceNumber: "TF-2026-WP-00001",
    officerId: 5,
    officerName: "P. K. Silva",
    officerDistrict: "WP",
    categoryCode: "SPD01",
    categoryDescription: "Exceeding speed limit in urban area (< 20 km/h over)",
    amount: 1500.00,
    driverLicenseNo: "B1234567",
    driverName: "A. B. Perera",
    vehicleNumber: "CAR-1234",
    district: "WP",
    location: "Colombo 03, Marine Drive",
    status: "PAID",
    issuedAt: "2026-06-01T14:22:00"
  },
  {
    id: 2,
    referenceNumber: "TF-2026-WP-00002",
    officerId: 5,
    officerName: "P. K. Silva",
    officerDistrict: "WP",
    categoryCode: "SIG01",
    categoryDescription: "Jumping a red traffic signal",
    amount: 3000.00,
    driverLicenseNo: "C9876543",
    driverName: "D. S. Fernando",
    vehicleNumber: "BUS-5678",
    district: "WP",
    location: "Nugegoda Junction",
    status: "PENDING",
    issuedAt: "2026-06-02T16:10:00"
  },
  {
    id: 3,
    referenceNumber: "TF-2026-SP-00003",
    officerId: 8,
    officerName: "K. L. Ratnayake",
    officerDistrict: "SP",
    categoryCode: "ALC01",
    categoryDescription: "Driving under the influence of alcohol",
    amount: 25000.00,
    driverLicenseNo: "D4567890",
    driverName: "M. N. Mohamed",
    vehicleNumber: "WP-CAB-8899",
    district: "SP",
    location: "Galle Face, Colombo",
    status: "PAID",
    issuedAt: "2026-06-03T23:15:00"
  },
  {
    id: 4,
    referenceNumber: "TF-2026-CP-00004",
    officerId: 12,
    officerName: "A. R. Wickramasinghe",
    officerDistrict: "CP",
    categoryCode: "LIC01",
    categoryDescription: "Driving without a valid driving license",
    amount: 5000.00,
    driverLicenseNo: "E1122334",
    driverName: "T. G. Jayawardena",
    vehicleNumber: "CP-PC-4321",
    district: "CP",
    location: "Kandy Road, Peradeniya",
    status: "EXPIRED",
    issuedAt: "2026-05-15T09:45:00"
  },
  {
    id: 5,
    referenceNumber: "TF-2026-WP-00005",
    officerId: 5,
    officerName: "P. K. Silva",
    officerDistrict: "WP",
    categoryCode: "MOB01",
    categoryDescription: "Using mobile phone while driving",
    amount: 3000.00,
    driverLicenseNo: "F8877665",
    driverName: "S. K. Alwis",
    vehicleNumber: "WP-KV-9900",
    district: "WP",
    location: "Borella Junction",
    status: "PAID",
    issuedAt: "2026-06-04T11:20:00"
  },
  {
    id: 6,
    referenceNumber: "TF-2026-WP-00006",
    officerId: 3,
    officerName: "H. M. Herath",
    officerDistrict: "WP",
    categoryCode: "SFT01",
    categoryDescription: "Driver not wearing seatbelt",
    amount: 1000.00,
    driverLicenseNo: "G9900112",
    driverName: "J. R. Peiris",
    vehicleNumber: "CAR-9900",
    district: "WP",
    location: "Duplication Road, Col 03",
    status: "PAID",
    issuedAt: "2026-06-05T08:10:00"
  },
  {
    id: 7,
    referenceNumber: "TF-2026-SP-00007",
    officerId: 8,
    officerName: "K. L. Ratnayake",
    officerDistrict: "SP",
    categoryCode: "SPD03",
    categoryDescription: "Exceeding speed limit on highway (> 40 km/h over)",
    amount: 6000.00,
    driverLicenseNo: "H3344556",
    driverName: "W. M. Bandara",
    vehicleNumber: "WP-CAD-7766",
    district: "SP",
    location: "Southern Expressway (Galle Exit)",
    status: "PENDING",
    issuedAt: "2026-06-06T15:35:00"
  },
  {
    id: 8,
    referenceNumber: "TF-2026-NWP-00008",
    officerId: 15,
    officerName: "W. A. Kurera",
    officerDistrict: "NWP",
    categoryCode: "SFT03",
    categoryDescription: "Motorcyclist not wearing helmet",
    amount: 1500.00,
    driverLicenseNo: "J5566778",
    driverName: "R. A. Silva",
    vehicleNumber: "WP-BF-4455",
    district: "NWP",
    location: "Negombo Road, Kurunegala",
    status: "PAID",
    issuedAt: "2026-06-06T17:00:00"
  },
  {
    id: 9,
    referenceNumber: "TF-2026-CP-00009",
    officerId: 12,
    officerName: "A. R. Wickramasinghe",
    officerDistrict: "CP",
    categoryCode: "PRK01",
    categoryDescription: "Parking in a no-parking zone",
    amount: 1000.00,
    driverLicenseNo: "K9988776",
    driverName: "P. R. Kumara",
    vehicleNumber: "CP-PD-8877",
    district: "CP",
    location: "Dalada Veediya, Kandy",
    status: "PAID",
    issuedAt: "2026-06-07T10:15:00"
  },
  {
    id: 10,
    referenceNumber: "TF-2026-NP-00010",
    officerId: 21,
    officerName: "S. Yogarajan",
    officerDistrict: "NP",
    categoryCode: "SIG02",
    categoryDescription: "Ignoring a stop sign",
    amount: 1500.00,
    driverLicenseNo: "L2233445",
    driverName: "K. Selvakumar",
    vehicleNumber: "NP-QA-1122",
    district: "NP",
    location: "Hospital Road, Jaffna",
    status: "PAID",
    issuedAt: "2026-06-07T14:40:00"
  },
  {
    id: 11,
    referenceNumber: "TF-2026-EP-00011",
    officerId: 25,
    officerName: "M. I. M. Rizan",
    officerDistrict: "EP",
    categoryCode: "LIC03",
    categoryDescription: "Vehicle without valid revenue license",
    amount: 5000.00,
    driverLicenseNo: "M7766554",
    driverName: "A. H. M. Farook",
    vehicleNumber: "EP-QB-9988",
    district: "EP",
    location: "Main Street, Batticaloa",
    status: "PENDING",
    issuedAt: "2026-06-08T11:05:00"
  },
  {
    id: 12,
    referenceNumber: "TF-2026-WP-00012",
    officerId: 3,
    officerName: "H. M. Herath",
    officerDistrict: "WP",
    categoryCode: "SPD02",
    categoryDescription: "Exceeding speed limit in urban area (20-40 km/h over)",
    amount: 3000.00,
    driverLicenseNo: "N4455667",
    driverName: "C. D. Fernando",
    vehicleNumber: "WP-CAM-1133",
    district: "WP",
    location: "Galle Road, Mount Lavinia",
    status: "PAID",
    issuedAt: "2026-06-08T16:50:00"
  },
  {
    id: 13,
    referenceNumber: "TF-2026-UVA-00013",
    officerId: 30,
    officerName: "S. B. Dissanayake",
    officerDistrict: "UVA",
    categoryCode: "SFT01",
    categoryDescription: "Driver not wearing seatbelt",
    amount: 1000.00,
    driverLicenseNo: "P9988552",
    driverName: "D. M. Jayasundara",
    vehicleNumber: "UVA-PC-7788",
    district: "UVA",
    location: "Badulla Road, Bandarawela",
    status: "PAID",
    issuedAt: "2026-06-09T09:15:00"
  },
  {
    id: 14,
    referenceNumber: "TF-2026-SAB-00014",
    officerId: 34,
    officerName: "J. M. Basnayake",
    officerDistrict: "SAB",
    categoryCode: "SPD02",
    categoryDescription: "Exceeding speed limit in urban area (20-40 km/h over)",
    amount: 3000.00,
    driverLicenseNo: "Q1122556",
    driverName: "K. P. Pathirana",
    vehicleNumber: "SAB-PF-1234",
    district: "SAB",
    location: "Colombo-Ratnapura Road",
    status: "PAID",
    issuedAt: "2026-06-09T14:30:00"
  },
  {
    id: 15,
    referenceNumber: "TF-2026-WP-00015",
    officerId: 5,
    officerName: "P. K. Silva",
    officerDistrict: "WP",
    categoryCode: "ALC01",
    categoryDescription: "Driving under the influence of alcohol",
    amount: 25000.00,
    driverLicenseNo: "R8877112",
    driverName: "S. A. Perera",
    vehicleNumber: "WP-CAB-1212",
    district: "WP",
    location: "Havelock Road, Colombo 05",
    status: "PENDING",
    issuedAt: "2026-06-10T20:45:00"
  }
];

// Mock District Collections Breakdown
const MOCK_DISTRICTS = [
  { district: "WP", name: "Western Province", totalFines: 380, paidFines: 295, totalRevenue: 820000.00 },
  { district: "SP", name: "Southern Province", totalFines: 125, paidFines: 98, totalRevenue: 215000.00 },
  { district: "CP", name: "Central Province", totalFines: 110, paidFines: 82, totalRevenue: 180500.00 },
  { district: "SAB", name: "Sabaragamuwa Province", totalFines: 58, paidFines: 42, totalRevenue: 85000.00 },
  { district: "EP", name: "Eastern Province", totalFines: 40, paidFines: 30, totalRevenue: 62000.00 },
  { district: "UVA", name: "Uva Province", totalFines: 32, paidFines: 25, totalRevenue: 51000.00 },
  { district: "NP", name: "Northern Province", totalFines: 28, paidFines: 21, totalRevenue: 45000.00 }
];

// Mock Category Breakdown
const MOCK_CATEGORIES = [
  { code: "SPD01", description: "Speeding (< 20 km/h)", amount: 1500.00, count: 120, revenue: 180000.00 },
  { code: "SPD02", description: "Speeding (20-40 km/h)", amount: 3000.00, count: 85, revenue: 255000.00 },
  { code: "SPD03", description: "Speeding highway (> 40 km/h)", amount: 6000.00, count: 42, revenue: 252000.00 },
  { code: "SIG01", description: "Jumping red traffic signal", amount: 3000.00, count: 92, revenue: 276000.00 },
  { code: "SIG02", description: "Ignoring a stop sign", amount: 1500.00, count: 50, revenue: 75000.00 },
  { code: "SFT01", description: "Driver not wearing seatbelt", amount: 1000.00, count: 60, revenue: 60000.00 },
  { code: "SFT03", description: "Motorcyclist without helmet", amount: 1500.00, count: 45, revenue: 67500.00 },
  { code: "MOB01", description: "Using phone while driving", amount: 3000.00, count: 55, revenue: 165000.00 },
  { code: "LIC01", description: "No valid driving license", amount: 5000.00, count: 35, revenue: 175000.00 },
  { code: "LIC03", description: "No valid revenue license", amount: 5000.00, count: 18, revenue: 90000.00 },
  { code: "PRK01", description: "Parking in no-parking zone", amount: 1000.00, count: 80, revenue: 80000.00 },
  { code: "ALC01", description: "Drunk driving", amount: 25000.00, count: 8, revenue: 200000.00 }
];

// Mock Daily Trend (Last 10 Days)
const MOCK_TRENDS = [
  { date: "2026-06-01", finesCount: 42, revenue: 95000.00 },
  { date: "2026-06-02", finesCount: 38, revenue: 82000.00 },
  { date: "2026-06-03", finesCount: 55, revenue: 125000.00 },
  { date: "2026-06-04", finesCount: 48, revenue: 110000.00 },
  { date: "2026-06-05", finesCount: 62, revenue: 140000.00 },
  { date: "2026-06-06", finesCount: 50, revenue: 115000.00 },
  { date: "2026-06-07", finesCount: 35, revenue: 78000.00 },
  { date: "2026-06-08", finesCount: 58, revenue: 132000.00 },
  { date: "2026-06-09", finesCount: 65, revenue: 148000.00 },
  { date: "2026-06-10", finesCount: 72, revenue: 165000.00 }
];

export const mockAuthService = {
  login: async (username, password) => {
    await delay(600); // Simulate network delay
    if (username === "admin" && password === "admin123") {
      return {
        accessToken: "mock-jwt-admin-token-xyz789",
        role: "ADMIN",
        fullName: "Senior Dig. Director",
        username: "admin"
      };
    }
    throw new Error("Invalid username or password");
  }
};

export const mockAdminService = {
  getCollectionsSummary: async () => {
    await delay(300);
    const totalRevenue = MOCK_DISTRICTS.reduce((sum, d) => sum + d.totalRevenue, 0);
    const totalFines = MOCK_DISTRICTS.reduce((sum, d) => sum + d.totalFines, 0);
    const paidFines = MOCK_DISTRICTS.reduce((sum, d) => sum + d.paidFines, 0);
    
    // Simulate expired & pending
    const expiredCount = 38;
    const pendingCount = totalFines - paidFines - expiredCount;

    return {
      totalRevenue,
      totalFines,
      paidFinesCount: paidFines,
      pendingFinesCount: pendingCount,
      expiredFinesCount: expiredCount,
      paidPercentage: ((paidFines / totalFines) * 100).toFixed(1),
      pendingPercentage: ((pendingCount / totalFines) * 100).toFixed(1),
      expiredPercentage: ((expiredCount / totalFines) * 100).toFixed(1)
    };
  },

  getDistrictCollections: async () => {
    await delay(400);
    return [...MOCK_DISTRICTS].sort((a, b) => b.totalRevenue - a.totalRevenue);
  },

  getCategoryCollections: async () => {
    await delay(400);
    return [...MOCK_CATEGORIES].sort((a, b) => b.revenue - a.revenue);
  },

  getCollectionTrends: async () => {
    await delay(400);
    return [...MOCK_TRENDS];
  },

  getRecentFines: async () => {
    await delay(300);
    return [...MOCK_FINES];
  }
};
