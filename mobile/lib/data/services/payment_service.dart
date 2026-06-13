import 'package:dio/dio.dart';
import '../../core/api/dio_client.dart';
import '../../core/utils/constants.dart';
import '../models/payment_dto.dart';

class PaymentService {
  final Dio _dio = DioClient.instance;

  Future<PaymentDTO> processPayment({
    required String referenceNumber,
    required String categoryCode,
    required double amount,
    required String paymentMethod,
    required String paymentChannel,
    required String transactionRef,
  }) async {
    try {
      final response = await _dio.post(
        AppConstants.paymentsEndpoint,
        data: {
          'referenceNumber': referenceNumber,
          'categoryCode': categoryCode,
          'amount': amount,
          'paymentMethod': paymentMethod,
          'paymentChannel': paymentChannel,
          'transactionRef': transactionRef,
        },
      );
      return PaymentDTO.fromJson(response.data);
    } catch (e) {
      throw Exception('Payment processing failed. Please try again.');
    }
  }
}
