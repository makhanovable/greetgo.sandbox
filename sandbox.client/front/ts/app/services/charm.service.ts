import {Injectable} from "@angular/core";
import {Charm} from "../models/charm";
import {HttpService} from "../HttpService";

@Injectable()
export class CharmService {

    constructor(private http: HttpService) {
    }

    list: Charm[] = [];
    name: string = 'UNKNOWN';
    temp: string = 'UNKNOWN';
    attempt: number = 0;

    getCharms() {
        console.log("/client/get_charms");
        this.http.get("/client/get_charms").toPromise().then(result => {
            //alert(JSON.stringify(result.json()));
            for (let i = 0; i < Number(JSON.stringify(result.json().length)); i++) {
                let Charm = {
                    id: Number(JSON.stringify(result.json()[i].id)),
                    name: JSON.stringify(result.json()[i].name).replace(/["]+/g, ''),
                };
                this.list.push(Charm);
            }
        }, error => {
            alert("error");
        });
    }

    getCharmNameById(id): string {
        for (let i = 0; i < this.list.length; i++) {
            if (this.list[i].id == id) {
                this.temp = this.list[i].name;
                break;
            }
        }
        this.name = this.temp;
        this.temp = 'UNKNOWN';
        if (this.name == 'UNKNOWN' && this.attempt < 5) {
            this.getCharms();
            this.attempt++;
            return this.getCharmNameById(id);
        } else {
            this.attempt = 0;
            return this.name;
        }
    }

}