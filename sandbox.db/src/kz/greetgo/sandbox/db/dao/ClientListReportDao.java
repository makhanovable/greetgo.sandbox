package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientListReportDao {

  @Insert("INSERT INTO client_list_report (report_id_instance, person_id, client_record_request, file_type) " +
    "VALUES (#{report_id_instance}, #{instance.personId}, #{instance.requestBytes}, #{instance.fileTypeName})")
  void insert(@Param("report_id_instance") String report_id_instance,
              @Param("instance") ClientListReportInstance instance);

  @Select("SELECT person_id AS personId, " +
    "  client_record_request AS requestBytes, " +
    "  file_type AS fileTypeName " +
    "FROM client_list_report " +
    "WHERE report_id_instance = #{report_id_instance}")
  ClientListReportInstance selectInstance(@Param("report_id_instance") String report_id_instance);

  @Delete("DELETE FROM client_list_report WHERE report_id_instance = #{report_id_instance}")
  void delete(@Param("report_id_instance") String report_id_instance);
}
