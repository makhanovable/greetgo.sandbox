export class ClientAccountInfo {
  public id: number;
  public fullName: string;
  public charm: string;
  public age: number;
  public totalAccountBalance: number;
  public maxAccountBalance: number;
  public minAccountBalance: number;

  public assign(o: any): ClientAccountInfo {
    this.fullName = o.fullName;
    this.charm = o.charm;
    this.age = o.age;
    this.totalAccountBalance = o.totalAccountBalance;
    this.maxAccountBalance = o.maxAccountBalance;
    this.minAccountBalance = o.minAccountBalance;
    return this;
  }

  public static copy(a: any): ClientAccountInfo {
    let result = new ClientAccountInfo();
    result.assign(a);
    return result;
  }
}