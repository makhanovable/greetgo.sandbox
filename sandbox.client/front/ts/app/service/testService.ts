import {Injectable} from "@angular/core";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";


@Injectable()
export class TestService {

  value = new Subject;

  setValue(value){
    this.value = value;
  }

  getValue(): Observable<any>{
    return this.value.asObservable();
  }
}
