import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {AccountInfo} from "../../../../model/AccountInfo";
import {Observable} from "rxjs/Observable";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpService} from "../../../HttpService";

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


  loadAccountInfoList(pageIndex = 0, pageSize = 3, sortBy = '',sortDirection = 'asc', filter = '') {

    this.loadingSubject.next(true);

    console.log("PageIndex:"+pageIndex+", PageSize:"+pageSize+", SortBy:"+sortBy+", Sort:"+sortDirection+", Filter:"+filter);

    this.httpService.get("/accounts/").toPromise().then(response => {
      // console.log(response.json());
      this.accInfosSubject.next(response.json());
    }, error => {
      console.log(error);
    });

    this.loadingSubject.next(false);
  }

}