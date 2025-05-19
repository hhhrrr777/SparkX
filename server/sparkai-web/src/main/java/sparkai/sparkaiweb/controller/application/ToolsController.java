package sparkai.sparkaiweb.controller.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkai.service.service.interfaces.application.IToolService;

@RequestMapping("/api/tools")
@RestController
public class ToolsController {

    @Autowired
    IToolService iToolService;

    @GetMapping("/list")
    public void index() {

        iToolService.getToolList();
    }
}
