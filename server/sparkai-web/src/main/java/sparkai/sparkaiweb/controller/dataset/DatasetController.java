package sparkai.sparkaiweb.controller.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;
import sparkai.common.core.PageResult;
import sparkai.service.service.interfaces.dataset.IDatasetService;
import sparkai.service.vo.dataset.DatasetQueryVo;
import sparkai.service.vo.dataset.DatasetVo;

@RequestMapping("/api/dataset")
@RestController
public class DatasetController {

    @Autowired
    IDatasetService iDatasetService;

    @GetMapping("/index")
    public AjaxResult<PageResult<DatasetVo>> index(DatasetQueryVo queryVo) {

        return AjaxResult.success(iDatasetService.getDatasetList(queryVo));
    }
}