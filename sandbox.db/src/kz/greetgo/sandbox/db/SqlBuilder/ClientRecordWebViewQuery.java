package kz.greetgo.sandbox.db.SqlBuilder;

public class ClientRecordWebViewQuery extends ClientRecordCommonViewQuery {

  void limit(StringBuilder sb) {
    sb.append("limit ? offset ?");
  }
}
