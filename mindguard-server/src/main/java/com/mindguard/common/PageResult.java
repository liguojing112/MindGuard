package com.mindguard.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private Long pages;
    private Long current;
    private Long size;
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.total = page.getTotal();
        r.pages = page.getPages();
        r.current = page.getCurrent();
        r.size = page.getSize();
        r.records = page.getRecords();
        return r;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        PageResult<T> r = new PageResult<>();
        r.total = total;
        r.pages = (total + size - 1) / size;
        r.current = current;
        r.size = size;
        r.records = records;
        return r;
    }
}
