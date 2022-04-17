import {Manufacturer} from "./manufacturer";

export class Car {
    constructor(public id: number, public manufacturer: Manufacturer, public model: string, public price: number) {
    }
}