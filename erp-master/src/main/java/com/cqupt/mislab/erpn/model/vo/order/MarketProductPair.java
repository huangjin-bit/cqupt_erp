package com.cqupt.mislab.erpn.model.vo.order;

public class MarketProductPair {
    private String marketName;
    private String productName;

    // 构造函数，用于初始化市场和产品组合
    public MarketProductPair(String marketName, String productName) {
        this.marketName = marketName;
        this.productName = productName;
    }

    // 重写 equals 方法，用于比较两个 MarketProductPair 对象是否相等
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketProductPair that = (MarketProductPair) o;
        return marketName.equals(that.marketName) && productName.equals(that.productName);
    }

    // 重写 hashCode 方法，用于在集合中正确存储和查找对象
    @Override
    public int hashCode() {
        int result = marketName.hashCode();
        result = 31 * result + productName.hashCode();
        return result;
    }
}

