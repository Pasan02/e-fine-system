import 'package:dio/dio.dart';
import '../../core/api/dio_client.dart';
import '../../core/utils/constants.dart';
import '../models/fine_dto.dart';

class FineService {
  final Dio _dio = DioClient.instance;

  Future<FineDTO> verifyFine(String referenceNumber, String categoryCode) async {
    try {
      final response = await _dio.get(
        AppConstants.fineVerifyEndpoint,
        queryParameters: {
          'referenceNumber': referenceNumber,
          'categoryCode': categoryCode,
        },
      );
      return FineDTO.fromJson(response.data);
    } catch (e) {
      throw Exception('Failed to verify fine details. Please check connection and try again.');
    }
  }
}
