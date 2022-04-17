import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from './model/user';

@Component({
    selector: 'app-root',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss']
})
export class AppComponent {

    currentUser?: User = undefined;

    constructor(private router: Router) { } 

    navigateHome() {
        this.router.navigate(['/home']);
    }

    navigateLogin() {
        this.router.navigate(['/login']);
    }

    performLogout() {

    }

}
