package org.example.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class APIExceptionHandler {
    //Errores generales
    @ExceptionHandler(APIException.class)
    public Mono<ResponseEntity<APIErrorResponse>> handleAPIException(APIException exception){
        APIErrorResponse response= new APIErrorResponse(exception.getStatus(), exception.getMessage());
        return Mono.just(ResponseEntity.ok(response));
    }

    //Error 500
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<APIErrorResponse>> handleGeneralException(Exception exception){
        APIErrorResponse response= new APIErrorResponse(500, "Error interno del servidor: "+exception.getMessage());
        return Mono.just(ResponseEntity.ok(response));
    }

}
