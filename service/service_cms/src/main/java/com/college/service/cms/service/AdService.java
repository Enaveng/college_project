package com.college.service.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.service.cms.entity.Ad;
import com.baomidou.mybatisplus.extension.service.IService;
import com.college.service.cms.entity.vo.AdVo;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-29
 */
public interface AdService extends IService<Ad> {

    Page<AdVo> selectPage(Page<AdVo> pageParam);

    boolean removeAdImageById(String id);

    List<Ad> selectListById(String typeId);
}
