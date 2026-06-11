import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../data/models/payment_dto.dart';

class PaymentHistoryProvider with ChangeNotifier {
  static const String _historyKey = 'payment_history';
  List<PaymentDTO> _history = [];

  List<PaymentDTO> get history => _history;

  PaymentHistoryProvider() {
    _loadHistory();
  }

  Future<void> _loadHistory() async {
    final prefs = await SharedPreferences.getInstance();
    final historyJson = prefs.getStringList(_historyKey);
    
    if (historyJson != null) {
      _history = historyJson
          .map((item) => PaymentDTO.fromJson(json.decode(item)))
          .toList();
      notifyListeners();
    }
  }

  Future<void> addPayment(PaymentDTO payment) async {
    _history.insert(0, payment); // Add to the beginning
    
    final prefs = await SharedPreferences.getInstance();
    final historyJson = _history.map((item) => json.encode(item.toJson())).toList();
    await prefs.setStringList(_historyKey, historyJson);
    
    notifyListeners();
  }
}
