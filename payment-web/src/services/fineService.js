import axios from 'axios';

export const fineCategories = [
  { code: 'SPD01', shortLabel: 'Speeding low', amount: 1500 },
  { code: 'SPD02', shortLabel: 'Speeding medium', amount: 3000 },
  { code: 'SPD03', shortLabel: 'Speeding high', amount: 6000 },
  { code: 'SIG01', shortLabel: 'Red signal', amount: 3000 },
  { code: 'LIC01', shortLabel: 'No license', amount: 5000 },
  { code: 'ALC01', shortLabel: 'DUI', amount: 25000 },
];


const API_BASE_URL = 'http://localhost:8080/api';

export async function verifyFine(referenceNumber, categoryCode) {
  try {
    const response = await axios.get(`${API_BASE_URL}/fines/verify`, {
      params: {
        referenceNumber: referenceNumber.trim().toUpperCase(),
        categoryCode: categoryCode.trim().toUpperCase(),
      },
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.data && error.response.data.message) {
      throw new Error(error.response.data.message, { cause: error });
    }
    throw new Error('Failed to verify fine. Please check the identifiers or try again later.', { cause: error });
  }
}

export async function processPayment({
  referenceNumber,
  categoryCode,
  amount,
  paymentMethod,
  paymentChannel,
}) {
  try {
    // Generate a transaction ref (since we're simulating a payment gateway response)
    const transactionRef = `TXN-${Date.now()}-${Math.floor(Math.random() * 9000 + 1000)}`;

    const response = await axios.post(`${API_BASE_URL}/payments`, {
      referenceNumber: referenceNumber.trim().toUpperCase(),
      categoryCode: categoryCode.trim().toUpperCase(),
      amount: Number(amount),
      paymentMethod,
      paymentChannel,
      transactionRef,
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.data && error.response.data.message) {
      throw new Error(error.response.data.message, { cause: error });
    }
    throw new Error('Failed to process payment. Please try again.', { cause: error });
  }
}
