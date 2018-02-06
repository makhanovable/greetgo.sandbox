import {Component, EventEmitter, Output} from "@angular/core";
import {ClientDetails} from "../../model/ClientDetails";
import {HttpService} from "../HttpService";
import {Gender} from "../../model/Gender";
import {PhoneType} from "../../model/PhoneType";
import {Phone} from "../../model/Phone";
import {ClientDetailsToSave} from "../../model/ClientDetailsToSave";
import {ClientRecord} from "../../model/ClientRecord";

@Component({
  selector: 'client-details-component',
  template: require('./client-details-component.html'),
  styles: [require('./client-details-component.css')],
})

export class ClientDetailsComponent {
  @Output() onModalCloseOutput = new EventEmitter<any>();

  isVisible: boolean = false;
  isAnimating: boolean = false;
  clientDetails: ClientDetails;
  genderEnum = Gender;
  genderList: { [key: string]: string } = {};
  todayStringDate: string;
  minStringDate: string;
  phoneTypeEnum = PhoneType;
  phoneTypeList: { [key: string]: string } = {};
  phoneToAdd: Phone = new Phone();
  deletedPhoneList: Phone[] = [];
  curPhoneList: Phone[] = [];

  constructor(private httpService: HttpService) {
    this.genderList[Gender.EMPTY] = "Неизвестно";
    this.genderList[Gender.MALE] = "Мужской";
    this.genderList[Gender.FEMALE] = "Женский";

    this.todayStringDate = new Date().toJSON().split('T')[0];
    this.minStringDate = "1900-01-01";

    this.phoneTypeList[PhoneType.EMBEDDED] = "Встроенный";
    this.phoneTypeList[PhoneType.MOBILE] = "Мобильный";
    this.phoneTypeList[PhoneType.HOME] = "Домашний";
    this.phoneTypeList[PhoneType.WORK] = "Рабочий";
    this.phoneTypeList[PhoneType.OTHER] = "Другой";
  }

  show(clientRecordId: number) {
    this.deletedPhoneList = [];
    this.curPhoneList = [];
    this.setDefaultPhone();

    this.httpService.get("/client/details", {
      "clientRecordId": clientRecordId
    }).toPromise().then(result => {
      this.clientDetails = result.json() as ClientDetails;
      for (let phone of this.clientDetails.phones)
        this.curPhoneList.push(Phone.copy(phone));

      this.isVisible = true;
      setTimeout(() => this.isAnimating = true, 100);
    }, error => {
      console.log(error);
    });
  }

  hide(clientRecord: ClientRecord, isAddOperation: boolean) {
    this.isAnimating = false;
    setTimeout(() => this.isVisible = false, 300);
    setTimeout(() => this.onModalCloseOutput.emit({
      clientRecord: clientRecord,
      isAddOperation: isAddOperation
    }), 300);
  }

  onPhoneAppendButtonClick() {
    if (this.containsPhone(this.curPhoneList, this.phoneToAdd))
      return;

    let meetIdx: number = -1;
    this.deletedPhoneList.forEach((deletedPhone, index) => {
      if (Phone.compare(deletedPhone, this.phoneToAdd)) {
        meetIdx = index;
        return;
      }
    });

    if (meetIdx >= 0)
      this.deletedPhoneList.splice(meetIdx, 1);

    this.curPhoneList.push(Phone.copy(this.phoneToAdd));
    this.setDefaultPhone();
  }

  onPhoneRemoveButtonClick(phone: Phone, idx: number) {
    if (this.containsPhone(this.clientDetails.phones, phone))
      this.deletedPhoneList.push(phone);

    this.curPhoneList.splice(idx, 1);
  }

  //TODO: rawClientDetails скорее всего не нежно, т. к. используется ngModel связка с this.clientDetails
  onClientRecordFormSubmit(rawClientDetails: ClientDetails) {
    if (!this.isNameValid(this.clientDetails.surname) ||
      !this.isNameValid(this.clientDetails.name) ||
      !this.isStringDateValid(this.clientDetails.birthdate))
      return;

    let clientDetailsToSave = new ClientDetailsToSave();

    clientDetailsToSave.id = this.clientDetails.id;
    clientDetailsToSave.surname = this.normalizeName(this.clientDetails.surname);
    clientDetailsToSave.name = this.normalizeName(this.clientDetails.name);
    clientDetailsToSave.patronymic = this.normalizeName(this.clientDetails.patronymic);
    clientDetailsToSave.gender = this.clientDetails.gender;
    clientDetailsToSave.birthdate = this.clientDetails.birthdate;
    clientDetailsToSave.charmId = this.clientDetails.charmId;
    clientDetailsToSave.registrationAddressInfo = this.clientDetails.registrationAddressInfo;
    clientDetailsToSave.factualAddressInfo = this.clientDetails.factualAddressInfo;
    clientDetailsToSave.phones = [];
    clientDetailsToSave.deletedPhones = [];

    for (let curPhone of this.curPhoneList)
      if (!this.containsPhone(this.clientDetails.phones, curPhone))
        clientDetailsToSave.phones.push(Phone.copy(curPhone));

    for (let deletedPhone of this.deletedPhoneList)
      clientDetailsToSave.deletedPhones.push(Phone.copy(deletedPhone));

    this.httpService.post("/client/save", {
      "clientDetailsToSave": JSON.stringify(clientDetailsToSave)
    }).toPromise().then(result => {
      if (clientDetailsToSave.id == null)
        this.hide(result.json() as ClientRecord, true);
      else
        this.hide(result.json() as ClientRecord, false);

    }, error => {
      console.log(error);
    });
  }

  cancelClientRecordEdit() {
    this.hide(null, false);
  }

  //TODO: если нужно будет закрывать форму при нажатии вне нее, то раскомментить
  onContainerClicked(event: MouseEvent) {
    if ((<HTMLElement>event.target).classList.contains('modal')) {
      //this.hide();
    }
  }

  private containsPhone(phones: Phone[], phoneToSearch: Phone): boolean {
    for (let phone of phones)
      if (Phone.compare(phone, phoneToSearch))
        return true;

    return false;
  }

  private setDefaultPhone() {
    this.phoneToAdd.number = "+7";
    this.phoneToAdd.type = PhoneType.MOBILE;
  }

  private normalizeName(name: string): string {
    return name.trim();
  }

  private isNameValid(name: string): boolean {
    return this.normalizeName(name).length > 0;
  }

  private isStringDateValid(date: string): boolean {
    return date.length > 0 &&
      new Date(date) >= new Date(this.minStringDate) &&
      new Date(date) <= new Date(this.todayStringDate);
  }
}
