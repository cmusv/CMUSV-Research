package edu.cmu.smartcommunities.database.controller.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.hibernate.LockOptions;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import edu.cmu.smartcommunities.database.controller.DAOInterface;

public abstract class AbstractDAO<GenericType, Identifier extends Serializable>
   implements DAOInterface<GenericType, Identifier>
   {
   private Class<GenericType> persistentClass;
   private Session            session;

   @SuppressWarnings("unchecked")
   public AbstractDAO()
      {
      persistentClass = (Class<GenericType>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
      }

   public void clear()
      {
      getSession().clear();
      }

   public List<GenericType> findAll()
      {
      return findByCriteria();
      }

   /**
    * Use this inside subclasses as a convenience method.
    */

   @SuppressWarnings("unchecked")
   protected List<GenericType> findByCriteria(final Criterion... criterion)
      {
      final Criteria criteria = getSession().createCriteria(getPersistentClass());

      for (Criterion c:  criterion)
         {
         criteria.add(c);
         }
      return criteria.list();
      }

   @SuppressWarnings("unchecked")
   public List<GenericType> findByExample(final GenericType exampleInstance,
                                          final String[]    excludeProperty)
      {
      final Criteria criteria = getSession().createCriteria(getPersistentClass());
      final Example  example  = Example.create(exampleInstance);

      for (String exclude:  excludeProperty)
         {
         example.excludeProperty(exclude);
         }
      criteria.add(example);
      return criteria.list();
      }

   @SuppressWarnings("unchecked")
   public GenericType findById(final Identifier identifier,
                               final boolean    lock)
      {
      return (GenericType) (lock ? getSession().load(persistentClass,
                                                     identifier,
                                                     LockOptions.UPGRADE) :
                                   getSession().load(persistentClass,
                                                     identifier));
      }

   public void flush()
      {
      getSession().flush();
      }

   public Class<GenericType> getPersistentClass()
      {
      return persistentClass;
      }

   protected Session getSession()
      {
      if (session == null)
         {
         throw new IllegalStateException("Session has not been set on DAO before usage");
         }
      return session;
      }

   public GenericType makePersistent(final GenericType object)
      {
      getSession().saveOrUpdate(object);
      return object;
      }

   public void makeTransient(final GenericType object)
      {
      getSession().delete(object);
      }

   public void setSession(final Session session)
      {
      this.session = session;
      }
   }
