package sparkai.service.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import sparkai.common.core.IBaseMapper;
import sparkai.service.entity.system.SystemTokensEntity;

/**
 * 系统消耗token表 Mapper
 */
@Mapper
public interface SystemTokensMapper extends IBaseMapper<SystemTokensEntity> {

}
