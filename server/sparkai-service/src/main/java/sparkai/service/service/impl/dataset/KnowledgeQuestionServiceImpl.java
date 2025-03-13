package sparkai.service.service.impl.dataset;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.common.enums.SourceType;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkai.service.entity.dataset.KnowledgeQuestionEntity;
import sparkai.service.entity.dataset.KnowledgeQuestionParagraphEntity;
import sparkai.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkai.service.mapper.dataset.KnowledgeQuestionMapper;
import sparkai.service.mapper.dataset.KnowledgeQuestionParagraphMapper;
import sparkai.service.service.interfaces.dataset.IKnowledgeQuestionService;
import sparkai.service.task.EmbeddingQuestionTask;
import sparkai.service.vo.question.QuestionListVo;
import sparkai.service.vo.question.QuestionQueryVo;
import sparkai.service.vo.question.QuestionRelationVo;
import sparkai.service.vo.question.QuestionSaveVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeQuestionServiceImpl implements IKnowledgeQuestionService {

    @Autowired
    KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    EmbeddingQuestionTask questionTask;

    @Override
    public PageResult<QuestionListVo> getQuestionList(QuestionQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeQuestionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("content", queryVo.getContent());
        queryWrapper.orderByDesc("create_time");

        IPage<KnowledgeQuestionEntity> questionListRes =
                knowledgeQuestionMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<QuestionListVo> questionListVoList = new LinkedList<>();
        for (KnowledgeQuestionEntity entity : questionListRes.getRecords()) {
            QuestionListVo vo = new QuestionListVo();
            BeanUtils.copyProperties(entity, vo);

            // 关联的数量
            long num = knowledgeQuestionParagraphMapper.selectCount(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                    .eq("question_id", vo.getQuestionId()));
            vo.setLinkNum(num);

            questionListVoList.add(vo);
        }

        return PageResult.iPageHandle(questionListRes.getTotal(), pageNo, pageSize, questionListVoList);
    }

    /**
     * 添加问题
     * @param questionSaveVo QuestionSaveVo
     */
    @Override
    public void addQuestion(QuestionSaveVo questionSaveVo) {

        if (questionSaveVo.getContent().isBlank()) {
            throw new BusinessException("问题不能为空");
        }

        String[] questionList = questionSaveVo.getContent().split("\n");
        for (String question : questionList) {

            KnowledgeQuestionEntity questionEntity = new KnowledgeQuestionEntity();
            questionEntity.setQuestionId(IdUtil.randomUUID());
            questionEntity.setContent(question);
            questionEntity.setHitNums(0);
            questionEntity.setDatasetId(questionSaveVo.getDatasetId());
            questionEntity.setCreateTime(Tool.nowDateTime());

            knowledgeQuestionMapper.insert(questionEntity);
        }
    }

    /**
     * 获取关联信息
     * @param questionId String
     * @return List<QuestionRelationVo>
     */
    @Override
    public List<QuestionRelationVo> getRelationList(String questionId) {

        List<KnowledgeQuestionParagraphEntity> relationList =
                knowledgeQuestionParagraphMapper.selectList(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                        .eq("question_id", questionId));

        List<QuestionRelationVo> returnList = new LinkedList<>();
        for (KnowledgeQuestionParagraphEntity entity : relationList) {

            QuestionRelationVo vo = new QuestionRelationVo();
            BeanUtils.copyProperties(entity, vo);

            returnList.add(vo);
        }

        return returnList;
    }

    /**
     * 关联问题-段落
     * @param relationVo QuestionRelationVo
     */
    @Override
    public void doRelation(QuestionRelationVo relationVo) {

        // 新增段落关联
        if (relationVo.getType().equals(1)) {
            KnowledgeQuestionParagraphEntity questionParagraph = new KnowledgeQuestionParagraphEntity();
            questionParagraph.setUuid(IdUtil.randomUUID());
            questionParagraph.setDatasetId(relationVo.getDatasetId());
            questionParagraph.setDocumentId(relationVo.getDocumentId());
            questionParagraph.setParagraphId(relationVo.getParagraphId());
            questionParagraph.setQuestionId(relationVo.getQuestionId());
            questionParagraph.setCreateTime(Tool.nowDateTime());

            knowledgeQuestionParagraphMapper.insert(questionParagraph);

            // 向量化问题
            questionTask.executeAsyncTask(relationVo.getQuestionId(), relationVo.getParagraphId(), relationVo.getDocumentId());
        } else { // 删除关联

            knowledgeQuestionParagraphMapper.delete(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                    .eq("question_id", relationVo.getQuestionId())
                    .eq("paragraph_id", relationVo.getParagraphId()));

            // 删除embedding数据
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                    .eq("source_type", SourceType.QUESTION.getCode())
                    .eq("source_id", relationVo.getQuestionId())
                    .eq("paragraph_id", relationVo.getParagraphId()));
        }
    }
}
