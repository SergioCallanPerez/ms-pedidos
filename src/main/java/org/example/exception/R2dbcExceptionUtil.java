package org.example.exception;

import io.r2dbc.spi.R2dbcException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class R2dbcExceptionUtil {
    //Para la forma definida de errores desde la DB
    private static final Pattern CODE_PATTERN = Pattern.compile("ERROR\\[(\\d{3,})\\]:\\s*(.*)"); // Permite 3 o más dígitos

    public static APIException handleR2dbcException(Throwable exception) {
        if (exception instanceof R2dbcException r2dbcException) {
            String rawMessage = cleanMessage(r2dbcException.getMessage());
            String message = rawMessage;
            int status = 500; //Por defecto

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

        return new APIException("Error interno del servidor", 500);
    }

    private static String cleanMessage(String message) {
        if (message == null) return "Error interno del servidor";
        int idx = message.lastIndexOf(";");
        if (idx != -1 && idx + 1 < message.length()) {
            message = message.substring(idx + 1).trim();
        }
        return message;
    }
}