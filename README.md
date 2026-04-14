# PharmaTel Backend

Spring Boot 3 backend for PharmaTel (Java 17, Maven, JPA, Security JWT, Flyway, Lombok).

## Run

```bash
cd backend
mvn spring-boot:run
```

Server base URL: `http://localhost:8080/api`

## Configuration

Configuration is fully defined in `src/main/resources/application.yml` (no environment variables).

## Example curl

### Register patient
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"john","password":"pass123","role":"PATIENT","name":"John Doe","email":"john@example.com","phoneNumber":"+15550001"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"john","password":"pass123","role":"PATIENT"}'
```

### Medicines (filter + pagination)
```bash
curl "http://localhost:8080/api/medicines?name=para&page=0&size=10"
```

### Pharmacies nearby
```bash
curl "http://localhost:8080/api/pharmacies/nearby?lat=40.7128&lng=-74.0060"
```

### Create prescription (authenticated)
```bash
curl -X POST http://localhost:8080/api/prescriptions \
  -H "Authorization: Bearer <TOKEN>" \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"medicineId":1,"dose":"1 pill","frequency":"BID","startDate":"2026-01-01","endDate":"2026-01-10","scheduleTimes":["2026-01-01T08:00:00","2026-01-01T20:00:00"]}'
```

### Mark dose taken
```bash
curl -X POST http://localhost:8080/api/dose-schedules/1/take \
  -H "Authorization: Bearer <TOKEN>" \
  -H 'Content-Type: application/json' \
  -d '{"patientPersonalNote":"Taken after breakfast"}'
```

### Create observation
```bash
curl -X POST http://localhost:8080/api/observations \
  -H "Authorization: Bearer <TOKEN>" \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"doseScheduleId":1,"symptomMeasurementId":"11111111-1111-1111-1111-111111111111","valueNumeric":98.6}'
```

### Symptom definitions
```bash
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/symptoms/definitions
```
