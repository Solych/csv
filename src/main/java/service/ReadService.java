package service;

import exceptions.EmptyDbException;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;

/**
 * Created by Pavel on 27.04.2018.
 */
public interface ReadService {
    InputStreamResource read() throws EmptyDbException, IOException;
}
