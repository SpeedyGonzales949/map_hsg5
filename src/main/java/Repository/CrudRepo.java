package Repository;


import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * this class implements the interface for a Repository
 * @param <E> object type that will be stored in the Repository
 */
public interface CrudRepo<E> {
    /**
     * @param id id must not be null
     * @return the entity with the specified id or null - if there is no entity with the given id
     * @throws SQLException error when handling the database
     */
    E findOne(UUID id) throws SQLException;

    /**
     * @return all entities
     * @throws SQLException error when handling the database
     */
    List<E> findAll() throws SQLException;

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved otherwise returns the entity (id already exists)
     * @throws SQLException error when handling the database
     */
    E save(E entity) throws SQLException;

    /**
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws SQLException error when handling the database
     */
    E delete(UUID id) throws SQLException;

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated, otherwise returns the entity - (e.g id does not exist).
     * @throws SQLException error when handling the database
     */
    E update(E entity) throws SQLException;
}
