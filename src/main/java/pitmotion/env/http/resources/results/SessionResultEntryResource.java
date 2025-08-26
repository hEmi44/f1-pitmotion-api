package pitmotion.env.http.resources.results;

public record SessionResultEntryResource(
  Integer position,           // null pour FP si pas de classement
  String driverCode,
  String driverFirstName,
  String driverLastName,
  String teamCode,
  String teamName,
  Integer grid,               // null si non pertinent
  Integer laps,               // null si non pertinent
  String time,                // "1:22:09.123" ou null
  String gapToLeader,         // "+3.421" ou null
  String status,              // "Finished","DNF",...
  Double points,              // sprint/race

  String q1,
  String q2,
  String q3,

  Integer fastestLapNumber,
  String fastestLapTime,
  Double fastestLapAvgKph
) {}
