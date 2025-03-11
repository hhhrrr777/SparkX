// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.service.impl.dataset;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.core.PageResult;
import sparkai.common.utils.Tool;
import sparkai.service.entity.dataset.KnowledgeParagraphEntity;
import sparkai.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkai.service.service.interfaces.dataset.IKnowledgeParagraphService;
import sparkai.service.task.EmbeddingDocumentTask;
import sparkai.service.vo.paragraph.ParagraphListVo;
import sparkai.service.vo.paragraph.ParagraphQueryVo;
import sparkai.service.vo.paragraph.ParagraphVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeParagraphServiceImpl implements IKnowledgeParagraphService {

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    EmbeddingDocumentTask task;

    /**
     * 段落列表
     * @param queryVo ParagraphQueryVo
     * @return PageResult<ParagraphListVo>
     */
    @Override
    public PageResult<ParagraphListVo> getParagraphList(ParagraphQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeParagraphEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_id", queryVo.getDocumentId());
        queryWrapper.orderByDesc("create_time");

        IPage<KnowledgeParagraphEntity> paragraphListRes =
                knowledgeParagraphMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<ParagraphListVo> paragraphListVoList = new LinkedList<>();
        for (KnowledgeParagraphEntity entity : paragraphListRes.getRecords()) {
            ParagraphListVo vo = new ParagraphListVo();
            BeanUtils.copyProperties(entity, vo);

            paragraphListVoList.add(vo);
        }

        return PageResult.iPageHandle(paragraphListRes.getTotal(), pageNo, pageSize, paragraphListVoList);
    }

    /**
     * 激活、关闭段落
     * @param paragraphVo ParagraphVo
     */
    @Override
    public void activeParagraph(ParagraphVo paragraphVo) {

        KnowledgeParagraphEntity paragraph = knowledgeParagraphMapper.selectById(paragraphVo.getParagraphId());
        paragraph.setActive(paragraphVo.getActive());
        paragraph.setUpdateTime(Tool.nowDateTime());

        knowledgeParagraphMapper.updateById(paragraph);
    }

    /**
     * 编辑段落
     *
     * @param paragraphVo ParagraphVo
     */
    @Override
    public void editParagraph(ParagraphVo paragraphVo) {

        KnowledgeParagraphEntity paragraph = knowledgeParagraphMapper.selectById(paragraphVo.getParagraphId());
        boolean editFlag = false;
        // 是否做了修改
        if (!paragraph.getTitle().equals(paragraphVo.getTitle()) ||
                !paragraph.getContent().equals(paragraphVo.getContent())) {
            editFlag = true;
        }

        if (editFlag) {
            // 编辑内容
            paragraph.setTitle(paragraphVo.getTitle());
            paragraph.setContent(paragraphVo.getContent());
            paragraph.setUpdateTime(Tool.nowDateTime());

            knowledgeParagraphMapper.updateById(paragraph);

            // 执行向量化
            task.executeAsyncParagraphTask(paragraph.getUuid());
        }
    }
}
