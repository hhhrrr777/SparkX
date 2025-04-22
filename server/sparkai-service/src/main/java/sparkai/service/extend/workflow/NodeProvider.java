package sparkai.service.extend.workflow;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sparkai.common.exception.BusinessException;

@Slf4j
@Component
public class NodeProvider {

    /**
     * 根据节点名称去节点
     * @param nodeName String
     * @return IWorkflow
     */
    public IWorkflowNode handle(String nodeName) {

        try {

            String className = "sparkai.service.extend.workflow." + kebabToPascalCase(nodeName);
            // 使用Class.forName()获取Class对象
            Class<?> clazz = Class.forName(className);

            return (IWorkflowNode) clazz.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException e) {
            log.error("Class not found: " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 java.lang.reflect.InvocationTargetException e) {
            log.error("Failed to instantiate class: " + e.getMessage());
        }

        throw new BusinessException("系统异常");
    }

    private String kebabToPascalCase(String kebabCase) {
        if (StrUtil.isBlank(kebabCase)) {
            return "";
        }

        // Split the string by '-'
        String[] parts = kebabCase.split("-");
        StringBuilder pascalCase = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                // Capitalize the first letter and append to the result
                pascalCase.append(StrUtil.upperFirst(part));
            }
        }

        return pascalCase.toString();
    }
}