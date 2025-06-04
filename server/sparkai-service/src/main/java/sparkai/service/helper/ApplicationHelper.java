package sparkai.service.helper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkai.service.entity.application.ApplicationDatasetRelationEntity;
import sparkai.service.entity.dataset.KnowledgeDatasetEntity;
import sparkai.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkai.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkai.service.vo.dataset.DatasetSimpleVo;

import java.util.LinkedList;
import java.util.List;

@Component
public class ApplicationHelper {

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

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
}