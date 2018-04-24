package service;


import java.util.List;

/** Interface which must be implemented for repository
 *  Marked as generic, if will be added new model in future
 * Created by Pavel on 24.04.2018.
 * @param <T> class of model(Job now)
 */
public interface EntityService<T> {
    /**
     * Method for find all objects from db with using repository
     * @return list of model objects from db;
     */
    List<T> findAll();
}
