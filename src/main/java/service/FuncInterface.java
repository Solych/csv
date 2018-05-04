package service;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pavel on 27.04.2018.
 */
@FunctionalInterface
public interface FuncInterface<T, U> {
    T get(U u) throws IOException;

}
