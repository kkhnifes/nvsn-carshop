package at.kkhnifes.carshopbackend;

import at.kkhnifes.carshopbackend.apimodel.LoginModel;
import at.kkhnifes.carshopbackend.apimodel.RegisterModel;
import at.kkhnifes.carshopbackend.repository.DatabaseRepository;
import at.kkhnifes.carshopbackend.repository.model.Car;
import at.kkhnifes.carshopbackend.repository.model.Manufacturer;
import at.kkhnifes.carshopbackend.repository.model.User;
import at.kkhnifes.carshopbackend.util.HashUtil;
import at.kkhnifes.carshopbackend.util.JWTClaim;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashSet;

@Path("/api/carshop")
@RequestScoped
public class APIResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    DatabaseRepository repository;

    @PostConstruct
    public void init() {
        Manufacturer bmw = new Manufacturer("BMW");
        Manufacturer audi = new Manufacturer("Audi");
        Manufacturer nissan = new Manufacturer("Nissan");
        repository.addNewCar(new Car(bmw, "E21", 15100.00D));
        repository.addNewCar(new Car(bmw, "E36", 38868.00D));
        repository.addNewCar(new Car(bmw, "E39", 05500.00D));
        repository.addNewCar(new Car(bmw, "G30", 48400.00D));
        repository.addNewCar(new Car(audi, "A8 D2", 40350.00D));
        repository.addNewCar(new Car(audi, "A8 D3", 63560.00D));
        repository.addNewCar(new Car(audi, "A8 D5", 39320.00D));
        repository.addNewCar(new Car(nissan, "NV400", 27715.00D));
        repository.addNewCar(new Car(nissan, "200SX S13", 21325.00D));
        repository.addNewCar(new Car(nissan, "200SX S14", 26510.00D));
        System.out.println("Database initialized");
    }

    @GET
    @Path("getCars")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCars() {
        return Response.ok(repository.getCars()).build();
    }

    @POST
    @Path("login")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginModel loginModel) {
        String hashedPassword = HashUtil.hashString(loginModel.password);

        // Fetch the user from the database
        User user = repository.getUser(loginModel.userName);
        if (user != null) {
            if (hashedPassword == null || !hashedPassword.equals(user.getPasswordHash())) {
                return Response.status(401).build();
            }
        }

        // User does not exist
        else {
            return Response.status(404).build();
        }

        // Generate a new JWT for this user and return it
        String jwt = Jwt.issuer("https://carshop.at/")
                .upn("khnifes.kyrillus@gmail.com")
                .groups(new HashSet<>(Collections.singletonList("User")))
                .claim(Claims.nickname, loginModel.userName)
                .claim(JWTClaim.USER_ID.getValue(), user.getId())
                .sign();

        return Response.ok(new LoginModel.LoginResult(jwt)).build();
    }

    @POST
    @Path("register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterModel registerModel) {
        String hashedPassword = HashUtil.hashString(registerModel.password);

        // Check if a user with this name already exists
        User user = repository.getUser(registerModel.userName);
        if (user != null) {
            return Response.status(401).build();
        }

        // Persist the newly registered user
        user = new User(registerModel.userName, hashedPassword);
        repository.registerUser(user);
        return Response.ok().build();
    }
}