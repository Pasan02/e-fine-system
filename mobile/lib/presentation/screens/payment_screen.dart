import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../providers/fine_provider.dart';
import '../providers/payment_provider.dart';
import '../providers/payment_history_provider.dart';
import '../widgets/custom_button.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/snackbar_utils.dart';
import 'receipt_screen.dart';

class PaymentScreen extends StatefulWidget {
  const PaymentScreen({super.key});

  @override
  State<PaymentScreen> createState() => _PaymentScreenState();
}

class _PaymentScreenState extends State<PaymentScreen> {
  final _formKey = GlobalKey<FormState>();
  final _cardController = TextEditingController();
  final _expiryController = TextEditingController();
  final _cvvController = TextEditingController();
  
  String _paymentMethod = 'CARD';

  @override
  void dispose() {
    _cardController.dispose();
    _expiryController.dispose();
    _cvvController.dispose();
    super.dispose();
  }

  void _handlePayment() async {
    if (_formKey.currentState?.validate() ?? false) {
      final fineProvider = Provider.of<FineProvider>(context, listen: false);
      final paymentProvider = Provider.of<PaymentProvider>(context, listen: false);
      
      final fine = fineProvider.currentFine!;

      final success = await paymentProvider.processPayment(
        referenceNumber: fine.referenceNumber,
        categoryCode: fine.categoryCode,
        amount: fine.amount,
        paymentMethod: _paymentMethod,
      );

      if (success && mounted) {
        final lastPayment = paymentProvider.lastPayment;
        if (lastPayment != null) {
          Provider.of<PaymentHistoryProvider>(context, listen: false).addPayment(lastPayment);
        }
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(builder: (_) => const ReceiptScreen()),
        );
      } else if (mounted) {
        showSnackBar(context, paymentProvider.error ?? 'Payment failed', isError: true);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final fine = Provider.of<FineProvider>(context, listen: false).currentFine!;
    final currencyFormat = NumberFormat.currency(symbol: 'LKR ', decimalDigits: 2);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Secure Payment'),
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  'Amount to Pay',
                  style: Theme.of(context).textTheme.titleLarge,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                Text(
                  currencyFormat.format(fine.amount),
                  style: Theme.of(context).textTheme.displayLarge,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 32),
                Text(
                  'Payment Method',
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(fontSize: 16),
                ),
                const SizedBox(height: 16),
                Row(
                  children: [
                    Expanded(
                      child: _buildMethodSelector(
                        'CARD',
                        Icons.credit_card,
                        'Credit / Debit Card',
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: _buildMethodSelector(
                        'MOBILE_WALLET',
                        Icons.phone_android,
                        'Mobile Wallet',
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 32),
                if (_paymentMethod == 'CARD') ...[
                  CustomTextField(
                    label: 'Card Number',
                    hint: '0000 0000 0000 0000',
                    controller: _cardController,
                    prefixIcon: Icons.credit_card,
                    keyboardType: TextInputType.number,
                    validator: (v) => v!.isEmpty ? 'Required' : null,
                  ),
                  const SizedBox(height: 24),
                  Row(
                    children: [
                      Expanded(
                        child: CustomTextField(
                          label: 'Expiry Date',
                          hint: 'MM/YY',
                          controller: _expiryController,
                          keyboardType: TextInputType.datetime,
                          validator: (v) => v!.isEmpty ? 'Required' : null,
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: CustomTextField(
                          label: 'CVV',
                          hint: '123',
                          controller: _cvvController,
                          keyboardType: TextInputType.number,
                          isPassword: true,
                          validator: (v) => v!.isEmpty ? 'Required' : null,
                        ),
                      ),
                    ],
                  ),
                ] else ...[
                  CustomTextField(
                    label: 'Mobile Number',
                    hint: '07X XXX XXXX',
                    controller: _cardController,
                    prefixIcon: Icons.phone,
                    keyboardType: TextInputType.phone,
                    validator: (v) => v!.isEmpty ? 'Required' : null,
                  ),
                ],
                const SizedBox(height: 48),
                Consumer<PaymentProvider>(
                  builder: (context, paymentProvider, _) {
                    return CustomButton(
                      text: 'Pay ${currencyFormat.format(fine.amount)}',
                      icon: Icons.lock_outline,
                      isLoading: paymentProvider.isLoading,
                      onPressed: _handlePayment,
                    );
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildMethodSelector(String value, IconData icon, String label) {
    final isSelected = _paymentMethod == value;
    final theme = Theme.of(context);

    return InkWell(
      onTap: () {
        setState(() {
          _paymentMethod = value;
        });
      },
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 8),
        decoration: BoxDecoration(
          color: isSelected ? theme.primaryColor.withValues(alpha: 0.1) : Colors.white,
          border: Border.all(
            color: isSelected ? theme.primaryColor : const Color(0xFFE0E0E0),
            width: 2,
          ),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          children: [
            Icon(
              icon,
              color: isSelected ? theme.primaryColor : const Color(0xFF757575),
              size: 32,
            ),
            const SizedBox(height: 8),
            Text(
              label,
              textAlign: TextAlign.center,
              style: TextStyle(
                color: isSelected ? theme.primaryColor : const Color(0xFF757575),
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
