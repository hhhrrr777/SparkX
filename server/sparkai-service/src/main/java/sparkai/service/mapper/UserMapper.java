package sparkai.service.mapper;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.User;

@Mapper
public interface UserMapper extends IBaseMapper<User> {
}
