package sparkai.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationDatasetRelationEntity;
import sparkai.service.entity.application.ApplicationWorkflowRuntimeContextEntity;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkai.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.vo.dataset.DatasetSimpleVo;

import java.util.LinkedList;
import java.util.List;

@Component
public class ApplicationHelper {

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    /**
     * 获取关联的知识库
     * @param appId String
     * @return List<DatasetSimpleVo>
     */
    public List<DatasetSimpleVo> getRelationDatasetList(String appId) {

        List<DatasetSimpleVo> datasetSimpleVoList = new LinkedList<>();

        List<ApplicationDatasetRelationEntity> relationEntityList =
                applicationDatasetRelationMapper.selectList(new QueryWrapper<ApplicationDatasetRelationEntity>()
                        .eq("app_id", appId));
        if (!CollectionUtils.isEmpty(relationEntityList)) {

            List<String> datasetIds = relationEntityList.stream().map(ApplicationDatasetRelationEntity::getDatasetId).toList();
            List<KnowledgeDatasetEntity> datasetList = knowledgeDatasetMapper.selectByIds(datasetIds);
            for (KnowledgeDatasetEntity entity : datasetList) {

                DatasetSimpleVo vo = new DatasetSimpleVo();
                BeanUtils.copyProperties(entity, vo);

                datasetSimpleVoList.add(vo);
            }
        }

        return datasetSimpleVoList;
    }

    /**
     * 获取运行时上下文节点
     * @param runtimeId long
     * @param sourceId String
     * @param inputSourceId String
     * @return ApplicationWorkflowRuntimeContextEntity
     */
    public ApplicationWorkflowRuntimeContextEntity getRuntimeContext(long runtimeId, String sourceId, String inputSourceId) {

        ApplicationWorkflowRuntimeContextEntity returnContext = null;

        List<ApplicationWorkflowRuntimeContextEntity> context = applicationWorkflowRuntimeContextMapper.selectList(
                new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));

        if (context == null || context.isEmpty()) {
            return null;
        }

        // 如果上级节点只有一个，不存在并行节点，则直接返回
        if (context.size() == 1) {
            return context.get(0);
        }

        // 如果存在并行节点，则判断当前节点的输入是否是上个节点的输出，如果你是，则返回选定的上个节点，
        for (ApplicationWorkflowRuntimeContextEntity contextItem : context) {
            if (contextItem.getCell().equals(inputSourceId)) {
                returnContext = contextItem;
            }
        }

        // 如果不是，则直接返回任意一个上游节点，这里选第一个，因为上下文输入中保存了全部上游节点的输出
        if (returnContext == null) {
            return context.get(0);
        }

        return returnContext;
    }
}