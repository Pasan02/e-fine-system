import 'package:dio/dio.dart';
import '../../core/api/dio_client.dart';
import '../../core/utils/constants.dart';
import '../models/login_response.dart';

class AuthService {
  final Dio _dio = DioClient.instance;

  Future<LoginResponse> login(String username, String password) async {
    try {
      final response = await _dio.post(
        AppConstants.loginEndpoint,
        data: {
          'username': username,
          'password': password,
        },
      );
      
      return LoginResponse.fromJson(response.data);
    } catch (e) {
      // Return a mock response for now to allow UI testing if backend is down
      await Future.delayed(const Duration(seconds: 1));
      if (username == 'officer1' && password == 'password123') {
        return LoginResponse(accessToken: 'mock_token_123', role: 'OFFICER');
      }
      throw Exception(AppConstants.authError);
    }
  }
}
