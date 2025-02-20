package sparkai.service.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.system.UsersEntity;

@Mapper
public interface UserMapper extends IBaseMapper<UsersEntity> {
}
