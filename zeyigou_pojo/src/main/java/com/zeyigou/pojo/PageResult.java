package com.zeyigou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult <T> implements Serializable {
    private long total;         //总记录数
    private long totalPage;     //总页数
    private List<T> rows;        //每页记录集合
    private int page;           //当前页

    public void setRows(List rows) {
        this.rows = rows;
    }
    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }
}
