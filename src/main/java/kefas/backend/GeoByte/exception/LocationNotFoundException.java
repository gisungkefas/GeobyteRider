package kefas.backend.GeoByte.exception;

public class LocationNotFoundException extends RuntimeException{

    public LocationNotFoundException(Long id) {
        super("Location not found with id: " + id);
    }
}
