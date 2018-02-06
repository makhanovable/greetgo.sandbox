package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientListReportTestDao {
  @Select("SELECT EXISTS (SELECT TRUE FROM client_list_report WHERE report_instance_id = #{report_instance_id})")
  boolean selectReportInstanceIdExists(@Param("report_instance_id") String report_instance_id);

  @Delete("DELETE FROM client_list_report")
  void deleteAll();
}
