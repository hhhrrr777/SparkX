package sparkai.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class CensusVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户数
     */
    private Long userNum;

    /**
     * 提问数
     */
    private Long questionNum;

    /**
     * token数
     */
    private Long tokensNum;

    /**
     * 赞的数量
     */
    private Long likeNum;

    /**
     * 踩的数量
     */
    private Long dislikeNum;

    /**
     * 时间线
     */
    private List<String> timeLine;

    /**
     * 用户统计数据折线
     */
    private List<CensusSeriesVo> userSeries;

    /**
     * 问题统计数据折线
     */
    private List<CensusSeriesVo> questionSeries;

    /**
     * 用户tokens数据折线
     */
    private List<CensusSeriesVo> tokensSeries;

    /**
     * 评价数据折线
     */
    private List<CensusSeriesVo> appraiseSeries;

    @Data
    public static class CensusSeriesVo {

        private List<Long> data;

        private String type;

        private boolean smooth;
    }
}