export class ClientDetails {
  public id: string;
  public name: string;
  public surname: string;
  public patronymic: string;
  public phone: string;

  public assign(o: ClientDetails): ClientDetails{
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.phone = o.phone;
    return this;
  }

}