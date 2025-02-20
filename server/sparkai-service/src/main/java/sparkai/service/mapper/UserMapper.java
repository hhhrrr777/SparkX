package sparkai.service.mapper;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.UserEntity;

@Mapper
public interface UserMapper extends IBaseMapper<UserEntity> {
}
