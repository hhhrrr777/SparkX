package sparkai.service.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkai.common.enums.StatusEnum;
import sparkai.common.exception.BusinessException;
import sparkai.common.utils.Tool;
import sparkai.service.entity.system.SystemTeamUserEntity;
import sparkai.service.entity.system.SystemUsersEntity;
import sparkai.service.helper.UserContextHelper;
import sparkai.service.mapper.system.SystemTeamUserMapper;
import sparkai.service.mapper.system.SystemUserMapper;
import sparkai.service.service.interfaces.system.ITeamService;
import sparkai.service.validate.system.AddTeamUserValidate;
import sparkai.service.vo.system.LocalUserVo;
import sparkai.service.vo.system.TeamUserVo;
import sparkai.service.vo.system.UsersVo;

import java.util.Arrays;
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

    /**
     * 搜索用户
     * @param nickname String
     * @return List<UsersVo>
     */
    @Override
    public List<UsersVo> searchUser(String nickname) {

        if (nickname.isBlank()) {
            return new LinkedList<>();
        }

        List<SystemUsersEntity> userList = systemUserMapper.selectList(new QueryWrapper<SystemUsersEntity>()
                .like("nickname", nickname).eq("status", StatusEnum.YES.getCode()).eq("deleted", StatusEnum.YES.getCode()));

        List<UsersVo> returnList = new LinkedList<>();
        for (SystemUsersEntity user : userList) {
            UsersVo userVo = new UsersVo();
            BeanUtils.copyProperties(user, userVo);

            returnList.add(userVo);
        }

        return returnList;
    }

    /**
     * 添加团队成员
     * @param validate AddTeamUserValidate
     * @return String
     */
    @Override
    @Transactional
    public String addUser(AddTeamUserValidate validate) {

        if (validate.getUserIds().isBlank()) {
            throw new BusinessException("添加的用户不能为空");
        }

        List<String> addUserIds = Arrays.asList(validate.getUserIds().split(","));

        LocalUserVo localUser = UserContextHelper.getUser();
        for (String userId : addUserIds) {

            SystemTeamUserEntity teamUserEntity = new SystemTeamUserEntity();
            teamUserEntity.setTeamId(localUser.getTeamId());
            teamUserEntity.setUserId(userId);
            teamUserEntity.setCreateTime(Tool.nowDateTime());

            systemTeamUserMapper.insert(teamUserEntity);
        }

        return addUserIds.get(0);
    }
}