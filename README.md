curl -d{} http://localhost:8000/refresh
curl -d{} http://localhost:9999/refresh
curl -d "{\"reservationName\":\"Dr. Who1\"}" -H"content-type: application/json" http://localhost:9999/reservations
curl -d "{\"reservationName\":\"Dr. Syer\"}" -H"content-type: application/json" http://localhost:9999/reservations
curl -d "{\"reservationName\":\"Dr. Pollack\"}" -H"content-type: application/json" http://localhost:9999/reservations
curl -d "{\"reservationName\":\"Dr. Subramaina\"}" -H"content-type: application/json" http://localhost:9999/reservations

http://localhost:8000/reservations?page=1&size=3
http://localhost:8888/data-concierge-eureka-service/default
http://localhost:9999/data-concierge-service/reservations
http://localhost:9999/reservations/names






