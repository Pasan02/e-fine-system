import 'package:flutter/foundation.dart';
import '../../data/models/fine_dto.dart';
import '../../data/services/fine_service.dart';

class FineProvider with ChangeNotifier {
  final FineService _fineService = FineService();
  
  bool _isLoading = false;
  String? _error;
  FineDTO? _currentFine;

  bool get isLoading => _isLoading;
  String? get error => _error;
  FineDTO? get currentFine => _currentFine;

  void clearFine() {
    _currentFine = null;
    _error = null;
    notifyListeners();
  }

  Future<bool> verifyFine(String referenceNumber, String categoryCode) async {
    _isLoading = true;
    _error = null;
    _currentFine = null;
    notifyListeners();

    try {
      final fine = await _fineService.verifyFine(referenceNumber, categoryCode);
      _currentFine = fine;
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _error = "Could not verify fine. Please check reference number and category code.";
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }
}
