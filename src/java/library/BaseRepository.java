package library;

import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.Query;
// E ile genericslerden yararlanıyoruz
// BaseEntityyi extends eden entityler için kullanıyoruz Eyi, 
//Eye entity verince gelip burdan gerekli olanları kullanıyoruz

public class BaseRepository<E extends BaseEntity> {

    private final static String SELECT = "select %s from %s as %s";
    protected EntityManager entityManager;
    protected Class<E> entityClass;

    public BaseRepository(Class<E> entityClass) {
        this.entityClass = entityClass;
        entityManager = EntityManagers.getFactory().createEntityManager();
    }

    public void close() {
        entityManager.close();
    }

    public void persist(E entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    public void merge(E entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public void remove(long entityId) {
        E entity = entityManager.getReference(entityClass, entityId);
        entityManager.getTransaction().begin();
        entityManager.refresh(entity);
        entityManager.getTransaction().commit();
    }

    public E find(long entityId) {
        return entityManager.getReference(entityClass, entityId);
    }

    protected String createSelectJpql() {
        String entityName = entityClass.getSimpleName();
        String variableName = entityName.toLowerCase(Locale.US);
        String jpql = String.format(SELECT, variableName, entityName, variableName);
        return jpql;
    }

    public List<E> list() {
        String jpql = createSelectJpql();
        Query query = entityManager.createQuery(jpql);
        return query.getResultList();
    }
}
