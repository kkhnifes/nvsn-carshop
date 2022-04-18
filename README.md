# nvsn-carshop

# Grundidee
Die Grundidee dieses Projektes ist, ein Auto Webshop. 

## Jeder Benutzer kann

◦ sich registrieren

◦ sich die verfügbaren (d. s. Nicht-reservierte und nicht-verkaufte Autos) anschauen.


## • Jeder registrierte Benutzer

◦ kann sich anmelden

◦ kann sich ein oder mehrere Auto(s) reservieren

◦ kann sich ein Auto(s) kaufen

◦ kann ein gekauftes Auto abholen mit einem Termin. Danach verschwindet die
Kauftransaktion aus dem Bestand.Lediglich die Tatsache, dass Kunde X ein Auto vom
Typ Y geholt hat, verbleibt im Datenbesstand. Reiche Kunden lieben Diskretion!

# Anforderungen

## Technologieanforderung:

Backend: Quarkus

Frontend: Angular

## Anforderungen an die Infrastruktur:
• Alle Komponenten sind soweit möglich in Container zu geben, damit ein reibungsloser
Transfer auf andere Hardware erfolgen kann.

• Die Konfiguration dieser Infrastruktur soll zentral (wenn möglich in einer Datei erfolgen).

# Docker
Die Umsetzung von Docker funktioniert leider nur teilweise, da aus einem unerfindlichen Grund das Backend nicht erfolgreich gestartet werden kann.
<img src="https://imgur.com/VAYVp7m.png">

Die Konfigurations von Docker war keine große Herausforderung, da es zu den meisten Technologien eine Dokumentation gibt.

Für die Containerisierung des Backends wurde der Artikel von der Website https://quarkus.io/guides/building-native-image#creating-a-container-with-a-multi-stage-docker-build verwendet. Leider hat dieser zu keinem erfolgreichem Ergebnis geführt. 

Die Datenbank kann gestartet werden und funktioniert einwandfrei.
Es wird bei der Initialisierung mithilfe des MYSQL_DATABASE flags automatisch die Carshop Datenbank initialisiert.

```
services:
  database:
    container_name: carshop_database
    build: ./database
    ports:
      - "4500:3306"
    environment:
      MYSQL_ROOT_PASSWORD: server
      MYSQL_DATABASE: carshop
    networks:
      - car-shop-net
    
  quarkus:
    container_name: carshop_backend
    ports:
      - "8080:8080"
    build:
      context: ./CarShopBackend
      dockerfile: ./src/main/docker/Dockerfile.native
    networks:
      - car-shop-net
    depends_on:
      - database
    environment:
      - PORT=8080
      - QUARKUS_DATASOURCE_URL=jdbc:mysql://database:4500/carshop
      - MYSQL_ROOT_PASSWORD=server
networks:
  car-shop-net:
    driver: bridge
```

# Backend
Der Erste Schritt nach der Konifguration der Datenbank, war das Erstellen der Entitäten.
Es wurden vier  verschieden Enitities erstellt.

Car => id, model, price

Manufacturer => id, name

Reservation => id, car, user

User => id, name, passwordhash

Danach wurde das Repository ertellte welches mit Dependency Injection einen entityManager bekommt.
Diese Repository Klasse kümmert sich um alle Datenbank-Operationen.

Danach habe ich die Datenbank mit ein paar Testdaten gefüllt.
```
@Inject
private EntityManager em;

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
```
Der nächste Schritt war es die benötigten Funktionen, welche für das Frontend benötigt werden, zu implementieren.

Der letzte Schritt war es die REST API zu konfigurieren und die nötigen Endpoints zu erstellen. Um die repository Funktionen zu nutzen muss auch das repository injectet werde.
```
    @Inject
    DatabaseRepository repository;
    
    @GET
    @Path("getCars")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCars() {
        return Response.ok(repository.getCars()).build();
    }


```

# Frontend
Die verschiedenen Ansichten im Frontend wurden mit Material verschönert.
Als erstes wurde die Homepage initialisierst welche alle verfügbaren AUtos anzeigt.
<img src="https://imgur.com/H68AH46.png">
Danach kam die Login Anishcht und die register Ansicht. Diese Funktionen wurden mithilfe eines JWT realisiert. Ein Klassenkollege hat mich bei der Erstellung unterstützt da ich keine Erfahrung damit gehabt habe. 
<img src="https://imgur.com/sZsSb0G.png">
<img src="https://imgur.com/65d5REl.png">

Das Frontend ruft die verschiedenen Backend Endpoints über eine Service auf.

```
export class RestService {

    private api: string = "http://localhost:8080/api/carshop/";

    constructor(private client: HttpClient, private snack: MatSnackBar) {
        this.api = environment.apiUrl;
    }

    private defaultHeaders = { headers: new HttpHeaders({'content-type': 'application/json'}) }

    private authHeaders(): any {
        var bearer = localStorage.getItem('__bearer');
        return new HttpHeaders({
            'Authorization': `Bearer ${bearer}`
        });
    }

    private authJsonHeaders(): any {
        var bearer = localStorage.getItem('__bearer');
        return new HttpHeaders({
            'content-type': 'application/json',
            'Authorization': `Bearer ${bearer}`
        });
    }

    public login(model: AuthenticationModel): Observable<LoginResult> {
        return this.client.post<LoginResult>(this.api + "login", JSON.stringify(model), this.defaultHeaders);
    }

    public async register(model: AuthenticationModel): Promise<boolean> {
        return this.client.post(this.api + "register", JSON.stringify(model), this.defaultHeaders)
            .toPromise().catch(
                (error: HttpErrorResponse) => {
                    if (error.status == 404) {
                        this.snack.open("Unable to connect.", "Dismiss");
                    }
                    else if (error.status == 500) {
                        this.snack.open("An internal server error occurred.", "Dismiss");
                    }
                    return false;
                })
            .then(_ => true, _ => false);
    }

    public getCars(): Observable<Car[]> {
        return this.client.get<Car[]>(this.api + "getCars", this.defaultHeaders);
    }
}
```

# Datenbank
Bei der Datenbank habe ich mich diesmal für MySQL entschieden.
## Konfiguration
Für die Konfiguration mussten im Backend gewisse Atrribute gesetzt werden. Diese werden in der application.properties Datei gestzt.
```
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=root
quarkus.datasource.password=server
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:4500/carshop
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.dialect=org.hibernate.dialect.MariaDB53Dialect
```

Die Datenbank ist eine MySQL-Datenbank, welche fünf Tables enthält.

<img src="https://imgur.com/pJk6QLf.png">

Diese Tables spiegeln die Entities des Backend wieder.

Die Relationen werden mit Foreign Keys realisiert.
<img src="https://imgur.com/fP1OK37.png">