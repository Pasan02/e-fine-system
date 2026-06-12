import 'package:flutter/foundation.dart';
import '../../data/models/payment_dto.dart';
import '../../data/services/payment_service.dart';

class PaymentProvider with ChangeNotifier {
  final PaymentService _paymentService = PaymentService();
  
  bool _isLoading = false;
  String? _error;
  PaymentDTO? _lastPayment;

  bool get isLoading => _isLoading;
  String? get error => _error;
  PaymentDTO? get lastPayment => _lastPayment;

  void clearPayment() {
    _lastPayment = null;
    _error = null;
    notifyListeners();
  }

  Future<bool> processPayment({
    required String referenceNumber,
    required String categoryCode,
    required double amount,
    required String paymentMethod,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final String transactionRef = 'TXN-${DateTime.now().millisecondsSinceEpoch}';
      
      final payment = await _paymentService.processPayment(
        referenceNumber: referenceNumber,
        categoryCode: categoryCode,
        amount: amount,
        paymentMethod: paymentMethod,
        paymentChannel: 'MOBILE_APP',
        transactionRef: transactionRef,
      );
      
      _lastPayment = payment;
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _error = "Payment failed. Please try again later.";
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }
}
