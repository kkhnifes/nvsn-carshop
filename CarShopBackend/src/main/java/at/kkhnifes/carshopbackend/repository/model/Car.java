package at.kkhnifes.carshopbackend.repository.model;

import javax.persistence.*;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Manufacturer manufacturer;

    private String model;
    private double price;

    public Car() { }

    public Car(Manufacturer manufacturer, String model, double price) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
    }

    public long getId() {
        return this.id;
    }

    public Manufacturer getMake() {
        return this.manufacturer;
    }

    public void setMake(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String name) {
        this.model = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
