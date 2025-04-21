package sparkai.service.service.impl.workflow;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.utils.Tool;
import sparkai.service.entity.workflow.ApplicationWorkflowEntity;
import sparkai.service.mapper.workflow.ApplicationWorkflowMapper;
import sparkai.service.service.interfaces.workflow.IWorkflowService;
import sparkai.service.validate.workflow.SaveWorkflowValidate;
import sparkai.service.vo.workflow.SaveWorkflowVo;

@Service
public class WorkflowServiceImpl implements IWorkflowService {

    @Autowired
    ApplicationWorkflowMapper applicationWorkflowMapper;

    /**
     * 获取流程数据
     * @param appId String
     * @return SaveWorkflowVo
     */
    @Override
    public SaveWorkflowVo getFlowInfo(String appId) {

        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", appId));
        SaveWorkflowVo vo = new SaveWorkflowVo();
        BeanUtils.copyProperties(info, vo);

        return vo;
    }

    /**
     * 保存流程设计
     * @param validate SaveWorkflowValidate
     */
    @Override
    public void saveWorkflow(SaveWorkflowValidate validate) {

        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", validate.getAppId()));
        if (info == null) {
            ApplicationWorkflowEntity addEntity = new ApplicationWorkflowEntity();
            addEntity.setAppId(validate.getAppId());
            addEntity.setFlowData(validate.getFlowData());
            addEntity.setCreateTime(Tool.nowDateTime());

            applicationWorkflowMapper.insert(addEntity);
        } else {

            info.setFlowData(validate.getFlowData());
            info.setUpdateTime(Tool.nowDateTime());

            applicationWorkflowMapper.updateById(info);
        }
    }
}