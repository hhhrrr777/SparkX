package sparkai.service.mapper.application;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;

/**
 * 工作流运行时上下文 Mapper
 */
@Mapper
public interface ApplicationWorkflowRuntimeContextMapper extends IBaseMapper<ApplicationWorkflowRuntimeContextEntity> {

}
