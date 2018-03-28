import {Component, EventEmitter, Output, Input} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientDetails} from "../../model/ClientDetails";

@Component({
    selector: 'modal-view-component',
    template: require('./modal_view_component.html'),
    styles: [require ('./modal_view_component.css')],
})

export class ModalViewComponent {
    @Output() exit = new EventEmitter<void>();
    @Output() detailsFilled = new EventEmitter<ClientDetails>();

    @Input()selectedID: string;
    @Input()actionType: string;
    clientDetails: ClientDetails = new ClientDetails();
    charmRecords: String[] = null;

    constructor(private httpService: HttpService) {}

    ngOnInit() {
        // console.log(this.actionType);
        this.loadCharms();
        this.loadClientDetails();
    }

    //TODO: название функции несет неясную формулировку. По коду ты получаешь детальную информацию клента и видимо здесь появляется модалка.
    //Но, по названию кажется, что уже должно происходить само редактирование пользователя.
    loadClientDetails() {
        if (this.actionType == "edit") {
            let url = "/client/clientDetails/" + this.selectedID;
            this.httpService.get(url).toPromise().then(result => {
                this.clientDetails = ClientDetails.from(result.json() as ClientDetails);
            }, error => {
                console.log(error);
            });
        }
    }

    closeModal() {
        this.clientDetails.clearPar();
    }

    loadCharms() {
        this.httpService.get("/client/charms").toPromise().then(result => {
            console.log(result.json());
            this.charmRecords = result.json();
        }, error => {
            console.log(error);
        });
    }

    editAddClicked() {
        if (this.fieldsFilledCorrectly()) {
            this.detailsFilled.emit(this.clientDetails);
            this.closeModal();
        } else {
            alert("Заполните все необходимые поля, помеченные звездочкой");
        }
    }

    fieldsFilledCorrectly() : boolean {
        if (this.clientDetails.name != "" && this.clientDetails.surname != "" && this.clientDetails.gender != ""
            && this.clientDetails.birth_date != "" && this.clientDetails.charm != ""
            && this.clientDetails.rAdressStreet != "" && this.clientDetails.rAdressFlat != ""
            && this.clientDetails.rAdressHouse != "" && this.clientDetails.mobilePhones[0] != "") {
            return true;
        } else {
            return false;
        }
    }
}
