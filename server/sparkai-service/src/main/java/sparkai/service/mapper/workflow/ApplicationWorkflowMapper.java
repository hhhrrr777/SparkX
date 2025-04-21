package sparkai.service.mapper.workflow;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.workflow.ApplicationWorkflowEntity;

/**
 * 编排流程表 Mapper
 */
@Mapper
public interface ApplicationWorkflowMapper extends IBaseMapper<ApplicationWorkflowEntity> {

}
