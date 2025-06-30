package sparkai.service.helper;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
@Data
public class LicenseHelper implements ApplicationRunner {

    /**
     * 应用数量
     */
    private Integer AppNum;

    /**
     * 知识库数量
     */
    private Integer datasetNum;

    /**
     * 系统用户数量
     */
    private Integer userNum;

    /**
     * 授权公司名称
     */
    private String companyName;

    /**
     * 证书编号
     */
    private String licenseId;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        ClassPathResource resource = new ClassPathResource("license");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] licenseData =  line.split(":");
                if (licenseData[0].equals("APP_NUM")) {
                    this.AppNum = Integer.valueOf(licenseData[1]);
                } else if (licenseData[0].equals("DATASET_NUM")) {
                    this.datasetNum = Integer.valueOf(licenseData[1]);
                } else if (licenseData[0].equals("USER_NUM")) {
                    this.userNum = Integer.valueOf(licenseData[1]);
                } else if (licenseData[0].equals("COMPANY_NAME")) {
                    this.companyName = licenseData[1];
                } else if (licenseData[0].equals("LICENSE_ID")) {
                    this.licenseId = licenseData[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}