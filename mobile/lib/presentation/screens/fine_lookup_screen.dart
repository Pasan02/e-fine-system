import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/fine_provider.dart';
import '../widgets/custom_button.dart';
import '../widgets/custom_text_field.dart';
import '../widgets/snackbar_utils.dart';
import 'fine_details_screen.dart';
import 'payment_history_screen.dart';

class FineLookupScreen extends StatefulWidget {
  const FineLookupScreen({super.key});

  @override
  State<FineLookupScreen> createState() => _FineLookupScreenState();
}

class _FineLookupScreenState extends State<FineLookupScreen> {
  final _formKey = GlobalKey<FormState>();
  final _refController = TextEditingController();
  final _categoryController = TextEditingController();

  @override
  void dispose() {
    _refController.dispose();
    _categoryController.dispose();
    super.dispose();
  }

  void _handleLookup() async {
    if (_formKey.currentState?.validate() ?? false) {
      final fineProvider = Provider.of<FineProvider>(context, listen: false);
      final success = await fineProvider.verifyFine(
        _refController.text.trim(),
        _categoryController.text.trim().toUpperCase(),
      );

      if (success && mounted) {
        Navigator.of(context).push(
          MaterialPageRoute(builder: (_) => const FineDetailsScreen()),
        );
      } else if (mounted) {
        showSnackBar(context, fineProvider.error ?? 'Fine not found', isError: true);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lookup Fine'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.history),
            onPressed: () {
              Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const PaymentHistoryScreen()),
              );
            },
          ),
        ],
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor.withValues(alpha: 0.05),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Column(
                    children: [
                      Icon(Icons.search_rounded, size: 48, color: Theme.of(context).primaryColor),
                      const SizedBox(height: 16),
                      Text(
                        'Find Your Traffic Fine',
                        style: Theme.of(context).textTheme.titleLarge,
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Enter the details exactly as they appear on your fine sheet to verify and pay.',
                        style: Theme.of(context).textTheme.bodyMedium,
                        textAlign: TextAlign.center,
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),
                CustomTextField(
                  label: 'Reference Number',
                  hint: 'e.g. TF-2026-WP-00001',
                  controller: _refController,
                  prefixIcon: Icons.receipt_long,
                  validator: (value) => value == null || value.isEmpty ? 'Reference number is required' : null,
                ),
                const SizedBox(height: 24),
                CustomTextField(
                  label: 'Category Code',
                  hint: 'e.g. SPD01',
                  controller: _categoryController,
                  prefixIcon: Icons.category_outlined,
                  validator: (value) => value == null || value.isEmpty ? 'Category code is required' : null,
                ),
                const SizedBox(height: 40),
                Consumer<FineProvider>(
                  builder: (context, fineProvider, _) {
                    return CustomButton(
                      text: 'Verify Fine',
                      icon: Icons.check_circle_outline,
                      isLoading: fineProvider.isLoading,
                      onPressed: _handleLookup,
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
}
