package sparkai.service.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.common.enums.StatusEnum;
import sparkai.service.entity.system.SystemTeamUserEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.helper.UserContextHelper;
import sparkai.service.mapper.system.SystemTeamUserMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.system.ITeamService;
import sparkai.service.vo.system.LocalUserVo;
import sparkai.service.vo.system.TeamUserVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class TeamImpl implements ITeamService {

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    SystemUserMapper systemUserMapper;

    /**
     * 获取团队成员
     * @return List<TeamUserVo>
     */
    @Override
    public List<TeamUserVo> getTeamUserList() {

        LocalUserVo localUser = UserContextHelper.getUser();
        List<SystemTeamUserEntity> userList = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().eq("team_id", localUser.getTeamId()));

        List<TeamUserVo> teamUserList = new LinkedList<>();
        for (SystemTeamUserEntity teamUserEntity : userList) {

            TeamUserVo teamUserVo = new TeamUserVo();
            teamUserVo.setTeamId(teamUserEntity.getTeamId());
            teamUserVo.setUserId(teamUserEntity.getUserId());

            SystemUsersEntity userInfo = systemUserMapper.selectOne(new QueryWrapper<SystemUsersEntity>()
                    .eq("user_id", teamUserEntity.getUserId()));
            teamUserVo.setName(userInfo.getNickname());

            teamUserVo.setIsAdmin(localUser.getUserId().equals(teamUserEntity.getUserId())
                    ? StatusEnum.YES.getCode() : StatusEnum.NO.getCode());
            teamUserVo.setAppPermission(teamUserEntity.getAppPermission());
            teamUserVo.setDatasetPermission(teamUserEntity.getDatasetPermission());

            teamUserList.add(teamUserVo);
        }

        return teamUserList;
    }
}