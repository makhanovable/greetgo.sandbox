import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {Observable} from "rxjs/Observable";
import {BehaviorSubject} from "rxjs/BehaviorSubject";

export class GenericDataSource implements DataSource<any> {

  private dataSubject = new BehaviorSubject<any[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();

  connect(collectionViewer: CollectionViewer): Observable<any[]> {
    return this.dataSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.dataSubject.complete();
  }

  startLoading() {
    this.loadingSubject.next(true);
  }

  stopLoading() {
    this.loadingSubject.next(false);
  }

  updateDateSource(updatedDataSource) {
    this.dataSubject.next(updatedDataSource);

  }
}
