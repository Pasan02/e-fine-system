import 'package:flutter/foundation.dart';

class AppConstants {
  // Use 10.0.2.2 for Android emulator, localhost for Web/Chrome
  static const String baseUrl = kIsWeb ? 'http://localhost:8080/api' : 'http://10.0.2.2:8080/api';
  
  // Auth Endpoints
  static const String loginEndpoint = '/auth/login';
  
  // Fine Endpoints
  static const String finesEndpoint = '/fines';
  static const String fineVerifyEndpoint = '/fines/verify';
  
  // Payment Endpoints
  static const String paymentsEndpoint = '/payments';
  
  // Storage Keys
  static const String tokenKey = 'jwt_token';
  static const String userRoleKey = 'user_role';
  
  // Error Messages
  static const String genericError = 'Something went wrong. Please try again.';
  static const String networkError = 'No internet connection. Please check your network.';
  static const String authError = 'Authentication failed. Please check your credentials.';
}
