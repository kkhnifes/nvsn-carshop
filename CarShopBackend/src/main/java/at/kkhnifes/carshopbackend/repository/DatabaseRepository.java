package at.kkhnifes.carshopbackend.repository;

import at.kkhnifes.carshopbackend.repository.model.Car;
import at.kkhnifes.carshopbackend.repository.model.Manufacturer;
import at.kkhnifes.carshopbackend.repository.model.Reservation;
import at.kkhnifes.carshopbackend.repository.model.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Collection;

@ApplicationScoped
public class DatabaseRepository {

    @Inject
    private EntityManager em;

    public Collection<Car> getCars() {
        return em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
    }

    public Collection<Manufacturer> getMakes() {
        return em.createQuery("SELECT m FROM Manufacturer m", Manufacturer.class).getResultList();
    }

    @Transactional
    public void init() {
        Manufacturer tesla = new Manufacturer("TESLA");
        Manufacturer mercedes = new Manufacturer("Mercedes");
        Manufacturer polestar = new Manufacturer("Polestar");
        addManufacturer(tesla);
        addManufacturer(mercedes);
        addManufacturer(polestar);
        addCar(new Car(tesla, "MODEL Y", 15100.00D));
        addCar(new Car(tesla, "MODEL X", 38868.00D));
        addCar(new Car(tesla, "MODEL 3", 05500.00D));
        addCar(new Car(tesla, "MODEL S", 48400.00D));
        addCar(new Car(mercedes, "E-Klasse", 40350.00D));
        addCar(new Car(mercedes, "C-Klasse", 63560.00D));
        addCar(new Car(mercedes, "S-Klasse", 39320.00D));
        addCar(new Car(polestar, "1", 27715.00D));
        addCar(new Car(polestar, "2", 21325.00D));
        addCar(new Car(polestar, "3", 26510.00D));
    }

    @Transactional
    public void addCar(Car c) {
        em.persist(c);
    }

    @Transactional
    public void addManufacturer(Manufacturer m) {
        em.persist(m);
    }


    @Transactional
    public boolean addReservation(Car car, User user) {
        var r = getReservationByCarId(car);
        if (r != null) return false;
        em.persist(new Reservation(user, car));
        return true;
    }

    @Transactional
    public void removeReservation(int id) {
        var r = em.find(Reservation.class, id);
        em.remove(r);
    }

    public User getUserById(long userId) {
        return em.find(User.class, userId);
    }

    public User getUser(String username) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
        User user = query.setParameter("name", username).getResultList().stream().findFirst().orElse(null);
        return user;
    }

    public Collection<User> getUsers() {
        return em.createQuery("SELECT u FROM User u ", User.class).getResultList();
    }

    public Reservation getReservationByCarId(Car c) {
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r  WHERE r.car.id=:id", Reservation.class);
        return query.setParameter("id", c.getId()).getResultList().stream().findFirst().orElse(null);
    }

    @Transactional
    public void registerUser(User user) {
        em.persist(user);
    }
}
