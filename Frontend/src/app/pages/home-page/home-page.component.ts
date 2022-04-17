import { Component, OnInit } from '@angular/core';
import { Car } from 'src/app/model/car';
import { RestService } from 'src/app/service/rest.service';

@Component({
    selector: 'app-home-page',
    templateUrl: 'home-page.component.html',
    styleUrls: ['home-page.component.scss']
})
export class HomePageComponent implements OnInit {

    cars: Car[] = [ ];

    constructor(private rest: RestService) { }

    async ngOnInit() {
        var cars = await this.rest.getCars().toPromise();
        if (cars != undefined) {
            this.cars = cars;
        }
    }
}
