export class AccountInfo {
  public id: number/*int*/;
  public fullName: string;
  public charm: string;
  public age: number/*int*/;
  public totalAccBalance: number /*float*/;
  public maxAccBalance: number/*float*/;
  public minAccBalance: number/*float*/;

  public assign(o: any): AccountInfo {
    this.fullName = o.fullName;
    this.charm = o.charm;
    this.age = o.age;
    this.totalAccBalance = o.totalAccBalance;
    this.maxAccBalance = o.maxAccBalance;
    this.minAccBalance = o.minAccBalance;
    return this;
  }

  public static copy(a: any): AccountInfo {
    let result = new AccountInfo();
    result.assign(a);
    return result;
  }
}
