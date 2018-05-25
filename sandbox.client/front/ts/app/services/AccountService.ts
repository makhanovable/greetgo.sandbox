import {Injectable} from "@angular/core";
import {Subject} from "rxjs/Subject";
import {ClientAccountInfo} from "../../model/ClientAccountInfo";
import {HttpService} from "../HttpService";

@Injectable()
export class AccountService {

  private accountAddedSource = new Subject;
  private accountDeleteSource = new Subject;
  private accountUpdateSource = new Subject;

  accountAdded = this.accountAddedSource.asObservable();
  accountDeleted = this.accountDeleteSource.asObservable();
  accountUpdated = this.accountUpdateSource.asObservable();

  constructor(private httpService: HttpService) { }

  addNewAccount(accountInfo: ClientAccountInfo) {
    this.accountAddedSource.next(accountInfo);
  }

  deleteAccount(accountInfo: ClientAccountInfo) {
    this.accountDeleteSource.next(accountInfo);
  }

  updateAccount(accountInfo: ClientAccountInfo) {
    this.accountUpdateSource.next(accountInfo);
  }

}
