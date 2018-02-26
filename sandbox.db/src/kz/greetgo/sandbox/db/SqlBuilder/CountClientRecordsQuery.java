package kz.greetgo.sandbox.db.SqlBuilder;

public class CountClientRecordsQuery extends ClientRecordQueryBuilder {

  @Override
  void select(StringBuilder sb) {
    sb.append("select count(1)\n");
  }

  @Override
  void from(StringBuilder sb) {
    sb.append("from Client\n");
  }

  @Override
  void where(StringBuilder sb) {
    sb.append("where actual=true\n");
    if (withFilter)
      sb.append("and lower(concat(name, surname, patronymic)) SIMILAR TO ?\n");
  }
}
