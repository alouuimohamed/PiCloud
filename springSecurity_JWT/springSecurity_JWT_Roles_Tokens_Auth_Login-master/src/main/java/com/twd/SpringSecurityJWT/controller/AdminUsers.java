package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.dto.UserUpdateRequest;
import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.PlaceRepo;
import com.twd.SpringSecurityJWT.repository.ReservationRepo;
import com.twd.SpringSecurityJWT.service.AuthService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import com.twd.SpringSecurityJWT.service.OurUserDetailsService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@RestController
public class AdminUsers {

    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private OurUserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private ReservationRepo reservationRepo;



    @GetMapping("/public/place")
    public ResponseEntity<Object> getAllPlaces(){
        return ResponseEntity.ok(placeRepo.findAll());
    }



    @GetMapping("/admin/listUsers")
    public ResponseEntity<Object> getAllUsers(){
        return ResponseEntity.ok(ourUserRepo.findAll());
    }

    @GetMapping("/admin/listReservation")
    public ResponseEntity<Object> getAllreservations(){
        return ResponseEntity.ok(reservationRepo.findAll());
    }

    @PostMapping("/admin/saveplace")
    public ResponseEntity<Object> saveplace(@RequestBody ReqRes productRequest){
        Place placeToSave = new Place();
        placeToSave.setName(productRequest.getName());
        return ResponseEntity.ok(placeRepo.save(placeToSave));
    }
    @PostMapping("/admin/ban/{id}")
    public ResponseEntity<Object> banUser(@PathVariable Integer id) {
        return ResponseEntity.ok(authService.banUserById(id));
    }

    @GetMapping("/user/alone")
    public ResponseEntity<Object> userAlone(){
        return ResponseEntity.ok("USers alone can access this ApI only");
    }

    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminaAndUsersApi(){
        return ResponseEntity.ok("Both Admin and Users Can  access the api");
    }

    /** You can use this to get the details(name,email,role,ip, e.t.c) of user accessing the service*/
    @GetMapping("/public/email")
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); //get all details(name,email,password,roles e.t.c) of the user
        System.out.println(authentication.getDetails()); // get remote ip
        System.out.println(authentication.getName()); //returns the email because the email is the unique identifier
        return authentication.getName(); // returns the email
    }

//supressionn

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        // Vérifier si l'utilisateur existe
        if (!ourUserRepo.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur avec l'ID " + userId + " non trouvé.");
        }

        // Supprimer l'utilisateur
        ourUserRepo.deleteById(userId);

        return ResponseEntity.ok().body("L'utilisateur avec l'ID " + userId + " a été supprimé avec succès.");
    }

    @PostMapping("/public/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String tokenHeader) {
        ReqRes response = new ReqRes();

        try {
            // Vérification si le token existe dans l'en-tête Authorization
            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String token = tokenHeader.substring(7);

                // Recherche de l'utilisateur par le token
                Optional<Users> userOptional = ourUserRepo.findByToken(token);

                if (userOptional.isPresent()) {
                    // Suppression du token de l'utilisateur
                    Users user = userOptional.get();
                    user.setToken(null);
                    user.setOnline(false);
                    ourUserRepo.save(user);

                    // Réponse avec un message de déconnexion réussie
                    response.setStatusCode(200);
                    response.setMessage("User logged out successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token est invalide, renvoyer une erreur
                response.setStatusCode(400);
                response.setMessage("Invalid token format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PutMapping("/public/profile")
    public ResponseEntity<Object> updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest userUpdateRequest) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String tokenValue = token.substring(7);

                // Recherche de l'utilisateur par le token
                Optional<Users> userOptional = ourUserRepo.findByToken(tokenValue);
                if (userOptional.isPresent()) {
                    // Mise à jour des informations du profil de l'utilisateur
                    Users user = userOptional.get();
                    user.setName(userUpdateRequest.getName());
                    user.setPhone(userUpdateRequest.getPhone());
                    user.setAdress(userUpdateRequest.getAdress());

                    // Enregistrement des modifications dans la base de données
                    ourUserRepo.save(user);

                    // Réponse avec un message de succès
                    response.setStatusCode(200);
                    response.setMessage("User profile updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas valide, renvoyer une erreur
                response.setStatusCode(400);
                response.setMessage("Invalid token format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/user/add-reservation")
    public ResponseEntity<Object> addReservation(@RequestHeader("Authorization") String token,
                                                 @RequestParam Integer placeId,
                                                 @RequestParam String reservationDate) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Recherche de la place dans la base de données par son ID
                    Optional<Place> placeOptional = placeRepo.findById(placeId);
                    if (placeOptional.isPresent()) {
                        Place place = placeOptional.get();
                        // Vérifier si la place est déjà réservée
                        if (place.isReserved()) {
                            // Si la place est déjà réservée, renvoyer une erreur
                            response.setStatusCode(400);
                            response.setMessage("Place is already reserved.");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }

                        // Création de la réservation
                        Reservation reservation = new Reservation();
                        reservation.setUser(userOptional.get());
                        reservation.setPlace(place);

                        // Parsing de la date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(reservationDate);
                        reservation.setReservationDate(date);

                        // Marquer la place comme réservée
                        place.setReserved(true);

                        // Enregistrement de la réservation et de la mise à jour de la place dans la base de données
                        reservationRepo.save(reservation);
                        placeRepo.save(place);

                        // Réponse avec un message de succès
                        response.setStatusCode(200);
                        response.setMessage("Reservation added successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        // Si la place n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Place not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (ParseException e) {
            // En cas d'erreur de parsing de la date, renvoyer une erreur
            response.setStatusCode(400);
            response.setMessage("Invalid date format. Please provide the date in yyyy-MM-dd format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @DeleteMapping("/user/delete/reservation/{reservationId}")
    public ResponseEntity<Object> deleteReservation(@PathVariable Integer reservationId,
                                                    @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Recherche de la réservation dans la base de données par son ID
                    Optional<Reservation> reservationOptional = reservationRepo.findById(reservationId);
                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        Users user = userOptional.get();
                        // Vérifie si l'utilisateur associé au token est également l'utilisateur qui a créé la réservation
                        if (reservation.getUser().getId().equals(user.getId())) {
                            // Obtenez la place associée à la réservation
                            Place place = reservation.getPlace();
                            // Mettre à jour l'attribut isReserved de la place à false
                            place.setReserved(false);
                            // Enregistrez les modifications de la place dans la base de données
                            placeRepo.save(place);
                            // Supprimer la réservation de la base de données
                            reservationRepo.deleteById(reservationId);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Reservation deleted successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si l'utilisateur n'est pas autorisé à supprimer cette réservation, renvoyer une erreur
                            response.setStatusCode(403);
                            response.setMessage("User is not authorized to delete this reservation.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        // Si la réservation n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Reservation not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @PutMapping("/user/update/reservation/{reservationId}")
    public ResponseEntity<Object> updateReservation(@PathVariable Integer reservationId,
                                                    @RequestHeader("Authorization") String token,
                                                    @RequestParam(required = false) Integer newPlaceId,
                                                    @RequestParam(required = false) String newReservationDate) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Recherche de la réservation dans la base de données par son ID
                    Optional<Reservation> reservationOptional = reservationRepo.findById(reservationId);
                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        Users user = userOptional.get();
                        // Vérifie si l'utilisateur associé au token est également l'utilisateur qui a créé la réservation
                        if (reservation.getUser().getId().equals(user.getId())) {
                            // Vérifie si une nouvelle place a été spécifiée
                            if (newPlaceId != null) {
                                // Recherche de la nouvelle place dans la base de données par son ID
                                Optional<Place> newPlaceOptional = placeRepo.findById(newPlaceId);
                                if (newPlaceOptional.isPresent()) {
                                    // Mettre à jour la place de la réservation
                                    reservation.setPlace(newPlaceOptional.get());
                                } else {
                                    // Si la nouvelle place n'est pas trouvée, renvoyer une erreur
                                    response.setStatusCode(404);
                                    response.setMessage("New place not found.");
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                                }
                            }
                            // Vérifie si une nouvelle date de réservation a été spécifiée
                            if (newReservationDate != null) {
                                // Parsing de la nouvelle date
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date newDate = dateFormat.parse(newReservationDate);
                                // Mettre à jour la date de la réservation
                                reservation.setReservationDate(newDate);
                            }
                            // Enregistrement de la réservation mise à jour dans la base de données
                            reservationRepo.save(reservation);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Reservation updated successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si l'utilisateur n'est pas autorisé à modifier cette réservation, renvoyer une erreur
                            response.setStatusCode(403);
                            response.setMessage("User is not authorized to update this reservation.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        // Si la réservation n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Reservation not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (ParseException e) {
            // En cas d'erreur de parsing de la date, renvoyer une erreur
            response.setStatusCode(400);
            response.setMessage("Invalid date format. Please provide the date in yyyy-MM-dd format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}

