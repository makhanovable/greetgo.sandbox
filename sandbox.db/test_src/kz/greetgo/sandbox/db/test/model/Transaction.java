package kz.greetgo.sandbox.db.test.model;

public class Transaction {
  public String id;
  public String type;
  public String finished_at;
  public String money;
  public String transaction_type;
  public String account_number;
  public String error;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transaction)) return false;

    Transaction that = (Transaction) o;

    if (finished_at != null ? !finished_at.equals(that.finished_at) : that.finished_at != null) return false;
    if (transaction_type != null ? !transaction_type.equals(that.transaction_type) : that.transaction_type != null)
      return false;
    return account_number != null ? account_number.equals(that.account_number) : that.account_number == null;
  }

  @Override
  public int hashCode() {
    int result = finished_at != null ? finished_at.hashCode() : 0;
    result = 31 * result + (transaction_type != null ? transaction_type.hashCode() : 0);
    result = 31 * result + (account_number != null ? account_number.hashCode() : 0);
    return result;
  }
}
