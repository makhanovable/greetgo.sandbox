import {Injectable} from "@angular/core";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";
import {AccountInfo} from "../../model/AccountInfo";
import {HttpService} from "../HttpService";

@Injectable()
export class AccountService {

  private accountAddedSource = new Subject;

  accountAdded = this.accountAddedSource.asObservable();

  constructor(private httpService: HttpService) { }

  addNewAccount(accountInfo: AccountInfo) {
    this.accountAddedSource.next(accountInfo);
  }


  // getAccountInfoList(): Observable<AccountInfo[]> {
  //   const result = this.httpService.get("/accounts/");
  //   console.log(result);
  //   return result;
  // }
  //
  // setValue(value){
  //   this.value = value;
  // }
  //
  // getValue(): Observable<any>{
  //   return this.value.asObservable();
  // }
}
