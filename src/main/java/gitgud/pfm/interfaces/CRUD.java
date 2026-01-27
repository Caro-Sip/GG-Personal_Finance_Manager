package gitgud.pfm.interfaces;

public interface CRUD<T> {
    void create(T entity);
    T read(String id);
    void update(T entity);
    void delete(String id);
}
