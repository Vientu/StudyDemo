package com.zeyigou.group;



import com.zeyigou.pojo.TbSpecification;
import com.zeyigou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:  用于组合规格及规格选项的类
 * @Date: Create in 2019/4/24 09:16
 */
public class Specification implements Serializable {
    private TbSpecification spec;
    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpec() {
        return spec;
    }

    public void setSpec(TbSpecification spec) {
        this.spec = spec;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "spec=" + spec +
                ", specificationOptionList=" + specificationOptionList +
                '}';
    }
}
