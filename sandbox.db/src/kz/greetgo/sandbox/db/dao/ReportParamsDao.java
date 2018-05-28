package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.Phone;
import kz.greetgo.sandbox.controller.model.ReportParamsToSave;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReportParamsDao {
    @Select("Select * from report_params where report_id = #{report_id}")
    ReportParamsToSave getReportParams(@Param("report_id") int report_id);

    @Insert("insert into report_params (report_id, username, report_type, filterStr, sortBy, sortOrder) " +
            "values (#{report_id}, #{username}, #{report_type}, #{filterStr}, " +
            "#{sortBy}, #{sortOrder})")
    void insertReportParams(ReportParamsToSave reportParamsToSave);

    @Delete("delete from report_params where report_id = #{report_id}")
    void removeRepostParams(int report_id);
}
