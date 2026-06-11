import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../providers/fine_provider.dart';
import '../widgets/custom_button.dart';
import 'payment_screen.dart';

class FineDetailsScreen extends StatelessWidget {
  const FineDetailsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Fine Details'),
        elevation: 0,
      ),
      body: Consumer<FineProvider>(
        builder: (context, fineProvider, _) {
          final fine = fineProvider.currentFine;
          if (fine == null) {
            return const Center(child: Text('No fine details available'));
          }

          final currencyFormat = NumberFormat.currency(symbol: 'LKR ', decimalDigits: 2);
          final isPending = fine.status == 'PENDING';

          return SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor,
                    borderRadius: BorderRadius.circular(16),
                    boxShadow: [
                      BoxShadow(
                        color: Theme.of(context).primaryColor.withValues(alpha: 0.3),
                        blurRadius: 10,
                        offset: const Offset(0, 4),
                      ),
                    ],
                  ),
                  child: Column(
                    children: [
                      Text(
                        'Amount Due',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          color: Colors.white70,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        currencyFormat.format(fine.amount),
                        style: Theme.of(context).textTheme.displayLarge?.copyWith(
                          color: Theme.of(context).colorScheme.secondary,
                        ),
                      ),
                      const SizedBox(height: 16),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        decoration: BoxDecoration(
                          color: isPending ? Colors.orange.shade400 : Colors.green.shade400,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          fine.status,
                          style: const TextStyle(
                            color: Colors.white,
                            fontWeight: FontWeight.bold,
                            letterSpacing: 1.2,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),
                _buildDetailCard(
                  context,
                  title: 'Violation Details',
                  icon: Icons.gavel_rounded,
                  children: [
                    _buildDetailRow('Reference No', fine.referenceNumber),
                    _buildDetailRow('Category Code', fine.categoryCode),
                    _buildDetailRow('Description', fine.categoryDescription, isMultiLine: true),
                    _buildDetailRow('Date Issued', fine.issuedAt.split('T')[0]),
                  ],
                ),
                const SizedBox(height: 16),
                _buildDetailCard(
                  context,
                  title: 'Driver & Vehicle',
                  icon: Icons.directions_car_rounded,
                  children: [
                    _buildDetailRow('Driver Name', fine.driverName),
                    _buildDetailRow('License No', fine.driverLicenseNo),
                    _buildDetailRow('Vehicle No', fine.vehicleNumber),
                  ],
                ),
                const SizedBox(height: 40),
                if (isPending)
                  CustomButton(
                    text: 'Proceed to Payment',
                    icon: Icons.payment,
                    onPressed: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(builder: (_) => const PaymentScreen()),
                      );
                    },
                  ),
                if (!isPending)
                  CustomButton(
                    text: 'Already Paid',
                    isSecondary: true,
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                  ),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _buildDetailCard(BuildContext context, {required String title, required IconData icon, required List<Widget> children}) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: Theme.of(context).primaryColor),
                const SizedBox(width: 12),
                Text(
                  title,
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 18),
                ),
              ],
            ),
            const Divider(height: 32),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _buildDetailRow(String label, String value, {bool isMultiLine = false}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16.0),
      child: Row(
        crossAxisAlignment: isMultiLine ? CrossAxisAlignment.start : CrossAxisAlignment.center,
        children: [
          Expanded(
            flex: 2,
            child: Text(
              label,
              style: const TextStyle(
                color: Color(0xFF757575),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Expanded(
            flex: 3,
            child: Text(
              value,
              textAlign: TextAlign.right,
              style: const TextStyle(
                color: Color(0xFF1E1E1E),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
