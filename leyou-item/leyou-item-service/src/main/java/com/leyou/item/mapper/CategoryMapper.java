package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.Locale;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {
}
