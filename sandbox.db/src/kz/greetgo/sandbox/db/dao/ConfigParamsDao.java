package kz.greetgo.sandbox.db.dao;

import kz.greetgo.sandbox.controller.model.ReportParamsToSave;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ConfigParamsDao {
    @Select("Select page_max from config_params")
    int getPageMax();
}
