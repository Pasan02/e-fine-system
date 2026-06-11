class PaymentDTO {
  final int id;
  final int fineId;
  final String referenceNumber;
  final String driverName;
  final String vehicleNumber;
  final String categoryDescription;
  final double amountPaid;
  final String paymentMethod;
  final String paymentChannel;
  final String transactionRef;
  final String paidAt;

  PaymentDTO({
    required this.id,
    required this.fineId,
    required this.referenceNumber,
    required this.driverName,
    required this.vehicleNumber,
    required this.categoryDescription,
    required this.amountPaid,
    required this.paymentMethod,
    required this.paymentChannel,
    required this.transactionRef,
    required this.paidAt,
  });

  factory PaymentDTO.fromJson(Map<String, dynamic> json) {
    return PaymentDTO(
      id: json['id'] ?? 0,
      fineId: json['fineId'] ?? 0,
      referenceNumber: json['referenceNumber'] ?? '',
      driverName: json['driverName'] ?? '',
      vehicleNumber: json['vehicleNumber'] ?? '',
      categoryDescription: json['categoryDescription'] ?? '',
      amountPaid: (json['amountPaid'] ?? 0).toDouble(),
      paymentMethod: json['paymentMethod'] ?? '',
      paymentChannel: json['paymentChannel'] ?? '',
      transactionRef: json['transactionRef'] ?? '',
      paidAt: json['paidAt'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'fineId': fineId,
      'referenceNumber': referenceNumber,
      'driverName': driverName,
      'vehicleNumber': vehicleNumber,
      'categoryDescription': categoryDescription,
      'amountPaid': amountPaid,
      'paymentMethod': paymentMethod,
      'paymentChannel': paymentChannel,
      'transactionRef': transactionRef,
      'paidAt': paidAt,
    };
  }
}
