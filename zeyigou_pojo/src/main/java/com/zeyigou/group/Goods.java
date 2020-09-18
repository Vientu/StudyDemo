package com.zeyigou.group;



import com.zeyigou.pojo.TbGoods;
import com.zeyigou.pojo.TbGoodsDesc;
import com.zeyigou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 组合类
 * @Date: Create in 2019/4/26 09:54
 */
public class Goods implements Serializable {
    private TbGoods goods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> items;

    public Goods() {
        super();
    }
    public Goods(TbGoods goods, TbGoodsDesc goodsDesc) {
        super();
        this.goods = goods;
        this.goodsDesc = goodsDesc;
    }
    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItems() {
        return items;
    }

    public void setItems(List<TbItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goods=" + goods +
                ", goodsDesc=" + goodsDesc +
                ", items=" + items +
                '}';
    }
}
