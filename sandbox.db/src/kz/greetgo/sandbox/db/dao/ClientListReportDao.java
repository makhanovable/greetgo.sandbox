package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientListReportDao {

  @Insert("INSERT INTO client_list_report (report_id_instance, person_id, client_record_request, file_type) " +
    "VALUES (#{report_id_instance}, #{person_id}, #{client_record_request}, #{file_type})")
  void insert(@Param("report_id_instance") String report_id_instance,
              @Param("person_id") String person_id,
              @Param("client_record_request") byte[] client_record_request,
              @Param("file_type") String file_type);

  @Select("SELECT report_id_instance as reportIdInstance, " +
    "  person_id as personId, " +
    "  client_record_request as requestBytes, " +
    "  file_type as fileTypeName " +
    "FROM client_list_report " +
    "WHERE report_id_instance = #{report_id_instance}")
  ClientListReportInstance selectInstance(@Param("report_id_instance") String report_id_instance);

  @Delete("DELETE FROM client_list_report WHERE report_id_instance = #{report_id_instance}")
  void delete(@Param("report_id_instance") String report_id_instance);
}
