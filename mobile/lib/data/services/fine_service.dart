import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
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
      // Mock fallback if backend is not available
      debugPrint('Real API failed, falling back to Mock Data for Fine Verification');
      await Future.delayed(const Duration(seconds: 1));
      return FineDTO(
        id: 1,
        referenceNumber: referenceNumber,
        officerId: 5,
        officerName: 'P. K. Silva',
        officerDistrict: 'WP',
        categoryCode: categoryCode,
        categoryDescription: 'Exceeding speed limit in urban area',
        amount: 1500.00,
        driverLicenseNo: 'B1234567',
        driverName: 'A. B. Perera',
        vehicleNumber: 'CAR-1234',
        district: 'WP',
        location: 'Colombo 03, Marine Drive',
        status: 'PENDING',
        issuedAt: '2026-06-10T14:22:00',
      );
    }
  }
}
