import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../providers/payment_history_provider.dart';

class PaymentHistoryScreen extends StatelessWidget {
  const PaymentHistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Payment History'),
        elevation: 0,
      ),
      body: Consumer<PaymentHistoryProvider>(
        builder: (context, provider, _) {
          if (provider.history.isEmpty) {
            return const Center(
              child: Text(
                'No past payments found.',
                style: TextStyle(color: Colors.grey, fontSize: 16),
              ),
            );
          }

          final currencyFormat = NumberFormat.currency(symbol: 'LKR ', decimalDigits: 2);

          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: provider.history.length,
            itemBuilder: (context, index) {
              final payment = provider.history[index];
              final date = payment.paidAt.split('T')[0];

              return Card(
                margin: const EdgeInsets.only(bottom: 16),
                child: ListTile(
                  contentPadding: const EdgeInsets.all(16),
                  leading: CircleAvatar(
                    backgroundColor: Theme.of(context).primaryColor.withValues(alpha: 0.1),
                    child: Icon(Icons.receipt_long, color: Theme.of(context).primaryColor),
                  ),
                  title: Text(
                    payment.referenceNumber,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  subtitle: Padding(
                    padding: const EdgeInsets.only(top: 8.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('Date: $date'),
                        Text('Transaction: ${payment.transactionRef}'),
                      ],
                    ),
                  ),
                  trailing: Text(
                    currencyFormat.format(payment.amountPaid),
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: Theme.of(context).primaryColor,
                      fontSize: 16,
                    ),
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
