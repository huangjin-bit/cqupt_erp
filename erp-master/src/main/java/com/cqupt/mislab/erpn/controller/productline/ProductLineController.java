package com.cqupt.mislab.erpn.controller.productline;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.productline.ProductLineDTO;
import com.cqupt.mislab.erpn.service.productline.ProductLineService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/7/16 16:46
 */
@RestController
@RequestMapping("/productline")
public class ProductLineController {

    @Autowired
    private ProductLineService productLineService;


    @ApiOperation("新增生产线")
    @PostMapping("/addproductline")
    public Result addProductLine(HttpSession session,@RequestBody ProductLineDTO productLineDTO) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.insertProductLine(userUnique, productLineDTO);
    }

    @ApiOperation("撤销正在安装的生产线")
    @GetMapping("/removeproductline")
    public Result removeInstallingProduct(HttpSession session, Integer productLineId) {

        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.removeInstallProductLine(userUnique, productLineId);
    }

    @ApiOperation("获取生产线基本信息")
    @GetMapping("/basicProductLine")
    public Result basicProductLine() {
        return productLineService.basicProductLineVo();
    }

    @ApiOperation("开始生产")
    @GetMapping("/startproduct")
    public Result startProduce(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateBeginProduct(userUnique, productLineId);
    }

    @ApiOperation("暂停生产")
    @GetMapping("/suspendedproduct")
    public Result suspendedProduct(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateSuspendedProduct(userUnique, productLineId);
    }

    @ApiOperation("恢复生产")
    @GetMapping("/recoverproduct")
    public Result recoverProduct(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateRecoverProduct(userUnique, productLineId);
    }

    @ApiOperation("暂停安装生产线")
    @GetMapping("/suspendedproductline")
    public Result suspendedProductLine(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.UpdateSuspendedInstallation(userUnique, productLineId);
    }

    @ApiOperation("恢复安装生产线")
    @GetMapping("/recoverproductline")
    public Result recoverProductLine(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateBeginInstallation(userUnique, productLineId);
    }


    @ApiOperation("开始转产")
    @GetMapping("/beginchangeproduct")
    public Result beginChangeProduct(HttpSession session,@RequestParam Integer productLineId, String productName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateBeginChangeProduct(userUnique, productLineId, productName);
    }

    @ApiOperation("暂停转产")
    @GetMapping("/suspendedchangeproduct")
    public Result suspendedChangeProduct(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateSuspendChangeProduct(userUnique, productLineId);
    }

    @ApiOperation("恢复转产")
    @GetMapping("/recoverchangeproduct")
    public Result recoverChangeProduct(HttpSession session, Integer productLineId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.updateRecoverChangeProduct(userUnique, productLineId);
    }

    @ApiOperation("出售生产线")
    @GetMapping("/saleproductline")
    public Result sellProductLine(HttpSession session, Integer productLineId, String productLineType) {
        String userUnique = (String) session.getAttribute("userUnique");
        return productLineService.deleteSaleProductLine(userUnique, productLineId, productLineType);
    }
}
