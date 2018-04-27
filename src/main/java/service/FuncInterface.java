package service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pavel on 27.04.2018.
 */
@FunctionalInterface
public interface FuncInterface<T> {
    T get(InputStream stream) throws IOException;

}
