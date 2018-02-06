package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientListReportDao {

  @Insert("INSERT INTO client_list_report (report_instance_id, person_id, client_record_request, file_type) " +
    "VALUES (#{report_instance_id}, #{instance.personId}, #{instance.requestBytes}, #{instance.fileTypeName})")
  void insert(@Param("report_instance_id") String report_instance_id,
              @Param("instance") ClientListReportInstance instance);

  @Select("SELECT person_id AS personId, " +
    "  client_record_request AS requestBytes, " +
    "  file_type AS fileTypeName " +
    "FROM client_list_report " +
    "WHERE report_instance_id = #{report_instance_id}")
  ClientListReportInstance selectInstance(@Param("report_instance_id") String report_instance_id);

  @Delete("DELETE FROM client_list_report WHERE report_instance_id = #{report_instance_id}")
  void delete(@Param("report_instance_id") String report_instance_id);
}
