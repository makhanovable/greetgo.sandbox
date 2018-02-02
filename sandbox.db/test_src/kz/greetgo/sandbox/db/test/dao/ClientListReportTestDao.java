package kz.greetgo.sandbox.db.test.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ClientListReportTestDao {
  @Insert("INSERT INTO client_list_report (report_id_instance, person_id, client_record_request, file_type) " +
    "VALUES (#{token}, #{person_id}, #{client_record_request}, #{file_type}")
  void insert(@Param("report_id_instance") String report_id_instance,
              @Param("person_id") String person_id,
              @Param("client_record_request") byte[] client_record_request,
              @Param("file_type") String file_type);

  @Select("SELECT EXISTS (SELECT TRUE FROM client_list_report WHERE report_id_instance = #{report_id_instance})")
  boolean selectTokenExists(@Param("report_id_instance") String report_id_instance);

  @Delete("DELETE FROM client_list_report")
  void deleteAll();
}
