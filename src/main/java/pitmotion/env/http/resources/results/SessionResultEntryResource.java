package pitmotion.env.http.resources.results;

public record SessionResultEntryResource(
  Integer position,
  String driverCode,
  String driverFirstName,
  String driverLastName,
  String teamCode,
  String teamName,
  Integer grid,
  Integer laps,
  String time,
  String status,
  Double points,
  
  String q1,
  String q2,
  String q3
) {}
