package business.services;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;


@Stateless
public class ReservationServicesImpl implements ReservationServices {
    private EntityManager entityManager;
   
    @Inject public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager;}
}
