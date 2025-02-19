package sparkai.sparkaiweb.controller.home;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.common.core.AjaxResult;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("index")
@Slf4j
public class IndexController {

    @GetMapping("/index")
    public AjaxResult<Map<String, String>> index() {

        Map<String, String> res = new HashMap<>();
        res.put("tom", "jack");
        return AjaxResult.success(res);
    }
}
