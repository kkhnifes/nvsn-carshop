package at.kkhnifes.carshopbackend.repository.model;

import javax.persistence.*;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private User user;
    @OneToOne
    private Car car;

    public Reservation() {
    }

    public Reservation(User user, Car car) {
        this.user = user;
        this.car = car;
    }
}
