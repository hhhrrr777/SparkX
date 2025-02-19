package sparkai.common.core;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 基类Mapper
 * @param <T>
 */
@Mapper
public interface IBaseMapper<T> extends MPJBaseMapper<T> {

}
