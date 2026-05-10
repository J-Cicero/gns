package com.backend.gns.Shared.security.advice;

import com.backend.gns.Shared.security.exceptions.UserAlreadyExistException;
import com.backend.gns.Shared.security.exceptions.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class SecurityExceptionsHandler {

    private static final String ACCOUNT_LOCKED = "Votre compte a été bloqué. Veuillez contacter l'administration";
    private static final String METHOD_IS_NOT_ALLOWED = "Cette méthode de demande n'est pas autorisée sur ce point de terminaison. Veuillez envoyer une demande %s";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Une erreur s'est produite lors du traitement de la demande";
    private static final String INCORRECT_CREDENTIALS = "Mot de passe ou email incorrect. Veuillez réessayer";
    private static final String ACCOUNT_DISABLED = "Votre compte a été désactivé. S'il s'agit d'une erreur, veuillez contacter l'administration";
    private static final String ERROR_PROCESSING_FILE = "Une erreur s'est produite lors du traitement du fichier";
    private static final String NOT_ENOUGH_PERMISSION = "Vous n'avez pas assez d'autorisation";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Gère l'exception de compte désactivé.
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpErrorResponse> accountDisabledException() {
        return createHttpResponse(FORBIDDEN, ACCOUNT_DISABLED);
    }

    /**
     * Gère les mauvaises identifiants.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    /**
     * Gère les exceptions d'accès refusé.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpErrorResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    /**
     * Gère l'exception de compte bloqué. Statut HTTP 403 (Forbidden) est plus précis que 401 pour un compte connu mais restreint.
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpErrorResponse> lockedException() {
        return createHttpResponse(FORBIDDEN, ACCOUNT_LOCKED); // <-- CORRECTION: 403 Forbidden
    }

    // --- Exceptions JWT ---

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class})
    public ResponseEntity<HttpErrorResponse> jwtExceptions(Exception exception) {
        return createHttpResponse(UNAUTHORIZED, "Token invalide ou expiré: " + exception.getMessage());
    }

    // --- Exceptions Validation & Mapping ---

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpErrorResponse> noHandlerFoundException(NoHandlerFoundException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, "Cette page n'a pas été trouvée");
    }

    /**
     * Gère les échecs de validation des DTOs (e.g. @Valid dans le Controller).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGER.error(exception.getAllErrors().toString());
        return createValidationHttpResponse(exception.getFieldErrors());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpErrorResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod httpMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, httpMethod));
    }

    // --- Exceptions Base de Données et Service ---

    @ExceptionHandler({NoResultException.class, EntityNotFoundException.class, UserNotFoundException.class, RoleNotFoundException.class})
    public ResponseEntity<HttpErrorResponse> notFoundExceptions(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<HttpErrorResponse> userAlreadyExistException(UserAlreadyExistException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpErrorResponse> constraintViolationException(ConstraintViolationException exception) {
        LOGGER.error(exception.getMessage());
        LOGGER.error(exception.getConstraintName());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    // --- Exceptions Système/IO/Génériques ---

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<HttpErrorResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpErrorResponse> ioException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

    /**
     * Gestionnaire catch-all pour toutes les autres exceptions non gérées. Retourne 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> internalServerErrorException(Exception exception) {
        LOGGER.error("DETAILED ERROR: {}", exception.getMessage(), exception);
        String detailedMessage = INTERNAL_SERVER_ERROR_MSG + " - DEBUG: " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
        return createHttpResponse(INTERNAL_SERVER_ERROR, detailedMessage);
    }


    // --- Méthodes Utilitaire de Réponse ---

    private ResponseEntity<HttpErrorResponse> createHttpResponse(HttpStatus status, String message) {
        HttpErrorResponse httpResponse = new HttpErrorResponse();
        httpResponse.setStatus(status);
        httpResponse.setStatusCode(status.value());
        httpResponse.setMessage(message);
        httpResponse.setReason(status.getReasonPhrase());
        return new ResponseEntity<>(httpResponse, status);
    }

    private ResponseEntity<HttpErrorResponse> createValidationHttpResponse(List<FieldError> errors) {
        HttpErrorResponse httpResponse = new HttpErrorResponse();
        httpResponse.setStatus(BAD_REQUEST);
        httpResponse.setStatusCode(BAD_REQUEST.value());
        httpResponse.setMessage("Validation errors");
        httpResponse.setReason(BAD_REQUEST.getReasonPhrase());
        httpResponse.setValidations(errors);
        return new ResponseEntity<>(httpResponse, BAD_REQUEST);
    }

    //Gère l'unicité de l'email
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String message = exception.getMessage().toLowerCase();
        if (message.contains("unique") && (message.contains("email") || message.contains("user_email"))) {
            return createHttpResponse(BAD_REQUEST, "L'email que vous avez fourni est déjà utilisé par un autre compte.");
        }
        return createHttpResponse(BAD_REQUEST, "Une erreur d'intégrité des données s'est produite. Veuillez vérifier les informations.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = exception.getMessage();
        HttpStatus status;
        if (message != null && (message.contains("non trouvé") || message.contains("not found"))) {
            status = NOT_FOUND;
        } else {
            status = BAD_REQUEST;
        }
        return createHttpResponse(status, message);
    }
}