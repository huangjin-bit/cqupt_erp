package com.cqupt.mislab.erpn.controller.product;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.ProductBasic;
import com.cqupt.mislab.erpn.service.product.DevelopedProductService;
import com.cqupt.mislab.erpn.service.product.DevelopingProductService;
import com.cqupt.mislab.erpn.service.product.ProductBasicService;
import com.cqupt.mislab.erpn.service.product.UnDevelopProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 */
@Api("产品研发")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private DevelopingProductService developingProductService;

    @Autowired
    private UnDevelopProductService unDevelopProductService;

    @Autowired
    private DevelopedProductService developedProductService;
    @Autowired
    private ProductBasicService productBasicService;
    @ApiOperation("获取用户未研发的产品")
    @GetMapping("/undevelops")
    public Result getUnDevelops(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return unDevelopProductService.findUnDevelopProductByUserUnique(userUnique);
    }


    @ApiOperation("获取用户正在研发的产品")
    @GetMapping("/developing")
    public Result getDeveloping(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developingProductService.findDevelopingProductsByUserUnique(userUnique);
    }

    @ApiOperation("获取用户研发完成的产品")
    @GetMapping("/developeds")
    public Result getDeveloped(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developedProductService.findDevelopedProductsByUserUnique(userUnique);
    }

    @ApiOperation("开始研发")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/start")
    public Result startDevelop(HttpSession session, @RequestParam("productName") String productName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return unDevelopProductService.updateDevelopProduct(userUnique, productName);
    }

    @ApiOperation("暂停研发")
    @ApiImplicitParam(name = "productName", required = true, dataType = "String")
    @GetMapping("/stop")
    public Result stopDevelop(HttpSession session, @RequestParam("productName") String productName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developingProductService.updateStopDevelopProduct(userUnique, productName);
    }

    @ApiOperation("恢复研发")
    @ApiImplicitParam(name = "productName", required = true, dataType = "String")
    @GetMapping("/recovery")
    public Result recoveryDevelop(HttpSession session, @RequestParam("productName") String productName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developingProductService.updateRecoveryDevelopingProduct(userUnique, productName);
    }

    @ApiOperation("获取用户产品库存")
    @GetMapping("/products")
    public Result products(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productBasicService.findProductInventories(userUnique);
    }



}
