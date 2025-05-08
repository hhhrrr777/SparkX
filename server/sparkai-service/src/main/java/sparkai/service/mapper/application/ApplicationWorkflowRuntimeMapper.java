package sparkai.service.mapper.application;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeEntity;

/**
 * 流程运行时 Mapper
 */
@Mapper
public interface ApplicationWorkflowRuntimeMapper extends IBaseMapper<ApplicationWorkflowRuntimeEntity> {

}
