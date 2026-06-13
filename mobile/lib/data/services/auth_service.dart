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
      throw Exception(AppConstants.authError);
    }
  }
}
