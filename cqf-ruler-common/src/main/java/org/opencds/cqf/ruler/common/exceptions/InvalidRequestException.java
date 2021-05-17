package org.opencds.cqf.ruler.common.exceptions;

public class InvalidRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidRequestException(String message) {
        super(message);
    }
}