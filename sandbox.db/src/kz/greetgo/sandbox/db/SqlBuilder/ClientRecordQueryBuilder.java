package kz.greetgo.sandbox.db.SqlBuilder;

abstract public class ClientRecordQueryBuilder {
  public boolean withFilter;
  public String orderBy;
  public boolean desc;

  abstract void select(StringBuilder sb);

  abstract void from(StringBuilder sb);

  abstract void where(StringBuilder sb);

  public StringBuilder generateSql(StringBuilder sb) {
    select(sb);
    from(sb);
    where(sb);
    return sb;
  }
}


