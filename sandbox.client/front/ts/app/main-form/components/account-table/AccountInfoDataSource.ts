import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {AccountInfo} from "../../../../model/AccountInfo";
import {Observable} from "rxjs/Observable";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpService} from "../../../HttpService";
import {error} from "util";

export class AccountInfoDataSource implements DataSource<AccountInfo> {

  private accInfosSubject = new BehaviorSubject<AccountInfo[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  constructor(private httpService: HttpService) { }


  connect(collectionViewer: CollectionViewer): Observable<AccountInfo[]> {
    return this.accInfosSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.accInfosSubject.complete();
  }


  loadAccountInfoList() {

    this.loadingSubject.next(true);

    this.httpService.get("/accounts/").toPromise().then(response => {
      this.accInfosSubject.next(response.json());
    }, error => {
      console.log(error)
    });

    this.loadingSubject.next(false);
  }

}