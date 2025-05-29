package sparkai.service.service.impl.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkai.service.helper.UserContextHelper;
import sparkai.service.mapper.system.SystemTeamMapper;
import sparkai.service.service.interfaces.system.ITeamService;
import sparkai.service.vo.system.LocalUserVo;
import sparkai.service.vo.system.TeamUserVo;

import java.util.List;

@Service
public class TeamImpl implements ITeamService {

    @Autowired
    SystemTeamMapper systemTeamMapper;

    /**
     * 获取团队成员
     * @return List<TeamUserVo>
     */
    @Override
    public List<TeamUserVo> getTeamUserList() {

        LocalUserVo localUser = UserContextHelper.getUser();
        System.out.println("--------------------");
        System.out.println(localUser);
        System.out.println("--------------------");

        return null;
    }
}