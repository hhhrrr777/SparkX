package sparkai.service.service.interfaces.dataset;

import sparkai.service.vo.dataset.HitTestVo;
import sparkai.service.vo.dataset.SearchVo;

import java.util.List;

public interface IHitTestService {

    List<SearchVo> search(HitTestVo hitTestVo);
}
