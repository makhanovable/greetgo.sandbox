package kz.greetgo.sandbox.db.SqlBuilder;

public abstract class ClientRecordCommonViewQuery extends ClientRecordQueryBuilder {


  @Override
  void select(StringBuilder sb) {
    sb.append("select c.id, c.name, c.surname, c.patronymic, date_part('year', age(c.birthDate)) as age, c.charm, max(ca.money) AS maximumBalance, min(ca.money) AS minimumBalance, sum(ca.money) AS totalAccountBalance ");
  }

  @Override
  void from(StringBuilder sb) {
    sb.append("from Client c left join ClientAccount ca on ca.client=c.id\n");
  }

  @Override
  void where(StringBuilder sb) {
    sb.append("where c.actual=true and ca.actual=true\n");
    if (withFilter)
      sb.append("and lower(concat(name, surname, patronymic)) SIMILAR TO ?\n");
    sb.append("group by c.id\n");
  }

  public void orderBy(StringBuilder sb) {
    if (orderBy != null)
      sb.append("order by ").append(orderBy).append("\n");
    else
      sb.append("order by concat(name, surname, patronymic)\n");
    if (desc)
      sb.append("desc\n");
  }

  abstract void limit(StringBuilder sb);

  @Override
  public StringBuilder generateSql(StringBuilder sb) {
    super.generateSql(sb);
    orderBy(sb);
    limit(sb);
    return sb;
  }
}
