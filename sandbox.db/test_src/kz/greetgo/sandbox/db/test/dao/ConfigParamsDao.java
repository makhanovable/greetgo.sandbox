package kz.greetgo.sandbox.db.test.dao;

import kz.greetgo.sandbox.db.stand.model.PhoneDot;
import org.apache.ibatis.annotations.Insert;

public interface ConfigParamsDao {
    @Insert("insert into config_params (page_max) " +
            "values (#{page_max})")
    void insertPhone(int page_max);
}
