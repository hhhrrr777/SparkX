package sparkai.service.mapper.application;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.application.ApplicationCustomerEntity;

/**
 * 应用访客 Mapper
 */
@Mapper
public interface ApplicationCustomerMapper extends IBaseMapper<ApplicationCustomerEntity> {

}
