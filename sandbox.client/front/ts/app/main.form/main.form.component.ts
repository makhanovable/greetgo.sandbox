import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";

@Component({
    selector: 'main-form-component',
    template: require('./main.form.component.html')
    // template: `
    // <div>
    //   <h2>Main Form Component</h2>
    //
    //   <button (click)="exit.emit()">Выход</button>
    //
    //   <div *ngIf="!userInfo">
    //     <button [disabled]="!loadUserInfoButtonEnabled" (click)="loadUserInfoButtonClicked()">
    //       Загрузить данные пользователя
    //     </button>
    //     <div *ngIf="loadUserInfoError">
    //       {{loadUserInfoError}}
    //     </div>
    //   </div>
    //   <div *ngIf="userInfo">
    //
    //     <table>
    //       <tbody>
    //
    //       <tr>
    //         <td>ID</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.id}}</b></td>
    //       </tr>
    //       <tr>
    //         <td>Account name</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.accountName}}</b></td>
    //       </tr>
    //       <tr>
    //         <td>Surname</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.surname}}</b></td>
    //       </tr>
    //       <tr>
    //         <td>Name</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.name}}</b></td>
    //       </tr>
    //       <tr>
    //         <td>Patronymic</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.patronymic}}</b></td>
    //       </tr>
    //       <tr>
    //         <td>Phone type</td>
    //         <td>&nbsp;:&nbsp;</td>
    //         <td><b>{{userInfo.phoneType}}</b></td>
    //       </tr>
    //
    //       </tbody>
    //     </table>
    //
    //   </div>
    // </div>`,
})
export class MainFormComponent {
    @Output() exit = new EventEmitter<void>();

    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;

    constructor(private httpService: HttpService) {}

    loadUserInfoButtonClicked() {
        this.loadUserInfoButtonEnabled = false;
        this.loadUserInfoError = null;

        this.httpService.get("/auth/userInfo").toPromise().then(result => {
            this.userInfo = UserInfo.copy(result.json());
            let phoneType: PhoneType | null = this.userInfo.phoneType;
            console.log(phoneType);
        }, error => {
            console.log(error);
            this.loadUserInfoButtonEnabled = true;
            this.loadUserInfoError = error;
            this.userInfo = null;
        });
    }
}
