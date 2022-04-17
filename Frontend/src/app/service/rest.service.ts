import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Car } from '../model/car';

export class AuthenticationModel {
    constructor(public userName: string, public password: string) { }
}

export class LoginResult {
    constructor(public token: string) { }
}

@Injectable({
    providedIn: 'root'
})
export class RestService {

    private api: string = "<UNDEF>";

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
