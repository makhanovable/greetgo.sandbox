export class Phone {
  public id : number;
  public clientId : number;
  public number : string;
  public type : string;


  constructor(number: string, type: string) {
    this.number = number;
    this.type = type;
  }
}