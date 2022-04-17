import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthenticationModel, LoginResult, RestService } from 'src/app/service/rest.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    constructor(private formBuilder: FormBuilder, private rest: RestService, private snack: MatSnackBar, private router: Router) { }

    public loginForm: FormGroup;

    ngOnInit(): void {
        this.loginForm = this.formBuilder.group({
            userName: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    public async performLogin() {
        if (this.loginForm.valid) {
            this.rest.login(new AuthenticationModel(
                this.loginForm.get('userName')?.value,
                this.loginForm.get('password')?.value)
            ).toPromise().then(
                (loginResult) => {
                    localStorage.setItem('__bearer', loginResult!.token);
                    this.router.navigate(['/home'])
                        .then(_ => this.snack.open("Login successful", "Dismiss"))
                },
                (_: any) => this.snack.open("Invalid username or password", "Dismiss")
            );
        }
    }
}
