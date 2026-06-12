class FineDTO {
  final int id;
  final String referenceNumber;
  final int officerId;
  final String officerName;
  final String officerDistrict;
  final String categoryCode;
  final String categoryDescription;
  final double amount;
  final String driverLicenseNo;
  final String driverName;
  final String vehicleNumber;
  final String district;
  final String location;
  final String status;
  final String issuedAt;

  FineDTO({
    required this.id,
    required this.referenceNumber,
    required this.officerId,
    required this.officerName,
    required this.officerDistrict,
    required this.categoryCode,
    required this.categoryDescription,
    required this.amount,
    required this.driverLicenseNo,
    required this.driverName,
    required this.vehicleNumber,
    required this.district,
    required this.location,
    required this.status,
    required this.issuedAt,
  });

  factory FineDTO.fromJson(Map<String, dynamic> json) {
    return FineDTO(
      id: json['id'] ?? 0,
      referenceNumber: json['referenceNumber'] ?? '',
      officerId: json['officerId'] ?? 0,
      officerName: json['officerName'] ?? '',
      officerDistrict: json['officerDistrict'] ?? '',
      categoryCode: json['categoryCode'] ?? '',
      categoryDescription: json['categoryDescription'] ?? '',
      amount: (json['amount'] ?? 0).toDouble(),
      driverLicenseNo: json['driverLicenseNo'] ?? '',
      driverName: json['driverName'] ?? '',
      vehicleNumber: json['vehicleNumber'] ?? '',
      district: json['district'] ?? '',
      location: json['location'] ?? '',
      status: json['status'] ?? '',
      issuedAt: json['issuedAt'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'referenceNumber': referenceNumber,
      'officerId': officerId,
      'officerName': officerName,
      'officerDistrict': officerDistrict,
      'categoryCode': categoryCode,
      'categoryDescription': categoryDescription,
      'amount': amount,
      'driverLicenseNo': driverLicenseNo,
      'driverName': driverName,
      'vehicleNumber': vehicleNumber,
      'district': district,
      'location': location,
      'status': status,
      'issuedAt': issuedAt,
    };
  }
}
