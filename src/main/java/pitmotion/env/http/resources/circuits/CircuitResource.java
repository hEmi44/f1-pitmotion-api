package pitmotion.env.http.resources.circuits;

public record CircuitResource(
  String code,
  String name,
  String country,
  String city,
  Double lengthKm
) {}
