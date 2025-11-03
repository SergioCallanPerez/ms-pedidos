package org.example.exception;

import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class R2dbcExceptionUtil {
    //Para la forma definida de errores desde la DB
    private static final Pattern CODE_PATTERN = Pattern.compile("ERROR\\[(\\d{3,})\\]:\\s*(.*)");

    public static APIException handleR2dbcException(Throwable exception) {
        int status = 500;
        if (exception instanceof DataAccessResourceFailureException r2dbcException) {
            String rawMessage = cleanMessage(r2dbcException.getMessage());
            String message = rawMessage;
            // Por defecto

            Matcher matcher = CODE_PATTERN.matcher(rawMessage);
            if (matcher.find()) {
                try {
                    // Se intenta obtener el código de estado
                    status = Integer.parseInt(matcher.group(1));
                    message = matcher.group(2).trim();
                } catch (NumberFormatException e) {
                    // Se mantiene el status por defecto
                }
            }

            return new APIException(message, status);
        }

        else if (exception instanceof DataIntegrityViolationException r2dbcException) {
            String rawMessage = cleanMessage(r2dbcException.getMessage());
            String constraint = extractConstraintName(rawMessage);
            String field = ObtainConstrainField(constraint);
            if (rawMessage.contains("violates check constraint")) {
                status= 400;
                return new APIException("Valor inválido para el campo " + field, status);
            }
            return new APIException(rawMessage, status);
        }

        return new APIException("Error interno del servidor", status);
    }

    // Limpiar mensaje para claridad
    private static String cleanMessage(String message) {
        if (message == null) return "Error interno del servidor";
        int idx = message.lastIndexOf(";");
        if (idx != -1 && idx + 1 < message.length()) {
            message = message.substring(idx + 1).trim();
        }
        return message;
    }

    // Obtener nombre de la restricción
    private static String extractConstraintName(String message){
        Pattern constraintPattern = Pattern.compile("constraint\\s+\"([^\"]+)\"");
        Matcher matcher = constraintPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Error interno del servidor";
    }

    // Obtener el campo del constraint
    private static String ObtainConstrainField(String constraintName){
        // Pasar de productos_campo_check a campo
        String[] parts = constraintName.split("_");
        if (parts.length >= 2) {
            return parts[parts.length - 2];
        }
        return constraintName;
    }
}