package com.cqupt.mislab.erpn.controller.material;

import com.cqupt.mislab.erpn.dao.material.BuyingMaterialDao;
import com.cqupt.mislab.erpn.dao.material.MaterialOfUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.material.BuyingMaterialService;
import com.cqupt.mislab.erpn.service.material.MaterialOfUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * @author 张烈文
 * @date 2021/7/16 15:24
 */
@RestController
@RequestMapping("/material")
public class MaterialController {
    @Autowired
    private MaterialOfUserService materialOfUserService;

    @Autowired
    private BuyingMaterialService buyingMaterialService;


    @ApiOperation("获取用户还未到达的原料订单")
    @GetMapping("/unreachmaterial")
    public Result unReachMaterial(HttpSession session, @RequestParam(defaultValue = "1", value = "pageIndex") Integer pageIndex,
                                  @RequestParam(value = "pageSize", defaultValue = "4") Integer pageSize) {
        String userUnique = (String) session.getAttribute("userUnique");
        return buyingMaterialService.listBuyingMaterialUnReach(userUnique, pageIndex, pageSize);
    }

    @ApiOperation("获取用户原料信息及原料基本信息")
    @GetMapping("/materialbasic")
    public Result materialbasic(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return materialOfUserService.listMaterialVO(userUnique);
    }

    @ApiOperation("新增购买原料订单")
    @GetMapping("/savematerialorder")
    public Result saveMaterialOrder(HttpSession session, String materialName, Integer mNumber) {
        String userUnique = (String) session.getAttribute("userUnique");
        return buyingMaterialService.insertBuyingMaterial(userUnique, materialName, mNumber);
    }

    @ApiOperation("获取用户原料库存")
    @GetMapping("/materials")
    public Result materials(HttpSession session) {

        String userUnique = (String) session.getAttribute("userUnique");
        return materialOfUserService.listMaterialsByUserUnique(userUnique);

    }

}
