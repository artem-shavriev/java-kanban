package service;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(IOException e) {
    }
}
