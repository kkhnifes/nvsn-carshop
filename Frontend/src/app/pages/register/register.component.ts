import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthenticationModel, RestService } from 'src/app/service/rest.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

    constructor(private rest: RestService, private snack: MatSnackBar, private formBuilder: FormBuilder, private router: Router) { }

    public registerForm: FormGroup;

    ngOnInit(): void {
        this.registerForm = this.formBuilder.group({
            userName: ['', Validators.required],
            password: ['', Validators.required],
        });
    }

    public async performRegistration() {
        if (this.registerForm.valid) {
            var success = await this.rest.register(new AuthenticationModel(
                this.registerForm.get('userName')?.value,
                this.registerForm.get('password')?.value)
            );
            if (success) {
                this.router.navigate(['/login'])
                    .then(_ => this.snack.open("Account created successfully!", "Dismiss"));
            } else {
                this.snack.open("An account with this name already exists!", "Dismiss");
            }
        }
    }
}
