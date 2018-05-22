export class Phone {
  public id : number;
  public clientId : number;
  public number : string;
  public type : string;

  public isActive: boolean = true;

  constructor(clientId: number, number: string, type: string) {
    this.number = number;
    this.type = type;
  }
}