package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.controller.model.ReportParamsToSave;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ReportParamsDao {
    @Select("Select * from report_params where report_id = #{reportID}")
    ReportParamsToSave getReportParams(@Param("reportID") int reportID);

    @Insert("insert into report_params (report_id, username, report_type, filterStr, sortBy, sortOrder) " +
            "values (#{report_id}, #{username}, #{report_type}, #{filterStr}, " +
            "#{sortBy}, #{sortOrder}")
    void insertReportParams(ReportParamsToSave reportParamsToSave);

    @Delete("delete * from report_params where report_id = #{report_id}")
    void removeRepostParams(int report_id);
}
