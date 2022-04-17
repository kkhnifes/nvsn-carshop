package at.kkhnifes.carshopbackend.repository;

import at.kkhnifes.carshopbackend.repository.model.Car;
import at.kkhnifes.carshopbackend.repository.model.Manufacturer;
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
    private EntityManager entityManager;

    public Collection<Car> getCars() {
        return entityManager.createQuery("SELECT c FROM Car c", Car.class).getResultList();
    }

    public Collection<Manufacturer> getMakes() {
        return entityManager.createQuery("SELECT m FROM Manufacturer m", Manufacturer.class).getResultList();
    }

    @Transactional
    public void addNewCar(Car car) {
        // Persist the car make if it has not been persisted yet
        if (getMakes().stream().noneMatch(m -> m.getName().equals(car.getMake().getName()))) {
            entityManager.persist(car.getMake());
        }
        // Find the make in the database
        var make = getMakes()
                .stream()
                .filter(m -> m.getName().equals(car.getMake().getName()))
                .findFirst()
                .orElseThrow();

        // Check that the car does actually not exist
        if (getCars().stream().noneMatch(c -> c.getModel().equals(car.getModel())))
        {
            car.setMake(make);
            entityManager.persist(car);
        }
    }

    public User getUserById(long userId) {
        return entityManager.find(User.class, userId);
    }

    public User getUser(String username) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
        User user = query.setParameter("name", username).getResultList().stream().findFirst().orElse(null);
        return user;
    }

    public Collection<User> getUsers() {
        return entityManager.createQuery("SELECT u FROM User u ", User.class).getResultList();
    }

    @Transactional
    public void registerUser(User user) {
        entityManager.persist(user);
    }
}
