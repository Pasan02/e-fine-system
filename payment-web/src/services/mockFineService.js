const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

export const fineCategories = [
  { code: 'SPD01', shortLabel: 'Speeding low', amount: 1500 },
  { code: 'SPD02', shortLabel: 'Speeding medium', amount: 3000 },
  { code: 'SPD03', shortLabel: 'Speeding high', amount: 6000 },
  { code: 'SIG01', shortLabel: 'Red signal', amount: 3000 },
  { code: 'LIC01', shortLabel: 'No license', amount: 5000 },
  { code: 'ALC01', shortLabel: 'DUI', amount: 25000 },
]

const fineRecords = [
  {
    id: 1,
    referenceNumber: 'TF-2026-WP-00001',
    officerId: 5,
    officerName: 'P. K. Silva',
    officerDistrict: 'WP',
    categoryCode: 'SPD01',
    categoryDescription: 'Exceeding speed limit in urban area (< 20 km/h over)',
    amount: 1500,
    driverLicenseNo: 'B1234567',
    driverName: 'A. B. Perera',
    vehicleNumber: 'CAR-1234',
    district: 'WP',
    location: 'Colombo 03, Marine Drive',
    status: 'PENDING',
    issuedAt: '2026-06-10T14:22:00',
  },
  {
    id: 2,
    referenceNumber: 'TF-2026-CP-00018',
    officerId: 9,
    officerName: 'N. M. Fernando',
    officerDistrict: 'CP',
    categoryCode: 'SIG01',
    categoryDescription: 'Jumping a red traffic signal',
    amount: 3000,
    driverLicenseNo: 'C8765432',
    driverName: 'D. S. Silva',
    vehicleNumber: 'CAB-7788',
    district: 'CP',
    location: 'Nugegoda Junction',
    status: 'PENDING',
    issuedAt: '2026-06-10T16:40:00',
  },
  {
    id: 3,
    referenceNumber: 'TF-2026-GN-00007',
    officerId: 11,
    officerName: 'R. K. Jayasinghe',
    officerDistrict: 'GN',
    categoryCode: 'LIC01',
    categoryDescription: 'Driving without a valid driving license',
    amount: 5000,
    driverLicenseNo: 'N/A',
    driverName: 'K. T. Rodrigo',
    vehicleNumber: 'LOR-1188',
    district: 'GN',
    location: 'Gampaha Town',
    status: 'PAID',
    issuedAt: '2026-06-09T09:15:00',
  },
]

const paymentsByFineId = new Map()

const normalize = (value) => value.trim().toUpperCase()

const formatReceipt = (fine, paymentMethod, paymentChannel) => ({
  id: paymentsByFineId.size + 10,
  fineId: fine.id,
  referenceNumber: fine.referenceNumber,
  driverName: fine.driverName,
  vehicleNumber: fine.vehicleNumber,
  categoryDescription: fine.categoryDescription,
  amountPaid: fine.amount,
  paymentMethod,
  paymentChannel,
  transactionRef: `MOCK-TXN-${Date.now()}-${Math.floor(Math.random() * 9000 + 1000)}`,
  paidAt: new Date().toISOString(),
})

export async function verifyFine(referenceNumber, categoryCode) {
  await delay(500)

  const normalizedReference = normalize(referenceNumber)
  const normalizedCategory = normalize(categoryCode)
  const fine = fineRecords.find(
    (item) => item.referenceNumber === normalizedReference && item.categoryCode === normalizedCategory,
  )

  if (!fine) {
    throw new Error('No fine found matching both identifiers.')
  }

  if (fine.status !== 'PENDING') {
    throw new Error(`Fine is already ${fine.status}.`)
  }

  return { ...fine }
}

export async function processPayment({
  referenceNumber,
  categoryCode,
  amount,
  paymentMethod,
  paymentChannel,
}) {
  await delay(700)

  const normalizedReference = normalize(referenceNumber)
  const normalizedCategory = normalize(categoryCode)
  const fine = fineRecords.find(
    (item) => item.referenceNumber === normalizedReference && item.categoryCode === normalizedCategory,
  )

  if (!fine) {
    throw new Error('No fine found matching both identifiers.')
  }

  if (fine.status === 'PAID') {
    throw new Error('Fine is already PAID.')
  }

  if (fine.status === 'EXPIRED') {
    throw new Error('Fine is EXPIRED.')
  }

  if (Number(amount) !== Number(fine.amount)) {
    throw new Error(`Amount does not match the category price of LKR ${fine.amount.toLocaleString('en-LK')}.`)
  }

  if (paymentsByFineId.has(fine.id)) {
    throw new Error('A payment already exists for this fine.')
  }

  const receipt = formatReceipt(fine, paymentMethod, paymentChannel)
  paymentsByFineId.set(fine.id, receipt)
  fine.status = 'PAID'

  return { ...receipt }
}