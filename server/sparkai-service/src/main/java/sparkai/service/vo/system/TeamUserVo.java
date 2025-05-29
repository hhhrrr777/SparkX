package sparkai.service.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 团队id
     */
    private Integer teamId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 是否管理员
     */
    private Integer isAdmin;

    /**
     * 知识库权限
     */
    private String datasetPermission;

    /**
     * 应用权限
     */
    private String appPermission;
}