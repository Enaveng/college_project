package com.college.service.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.college.commonutils.result.R;
import com.college.service.cms.entity.Ad;
import com.college.service.cms.entity.vo.AdVo;
import com.college.service.cms.feign.OssFileService;
import com.college.service.cms.mapper.AdMapper;
import com.college.service.cms.service.AdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务实现类
 * </p>
 *
 * @author dlwlrma
 * @since 2023-07-29
 */
@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {

    @Autowired
    private OssFileService ossFileService;


    @Override
    public Page<AdVo> selectPage(Page<AdVo> pageParam) {
        //组装查询条件
        QueryWrapper<AdVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("a.type_id", "a.sort");
        List<AdVo> records = baseMapper.selectPageByQueryWrapper(pageParam, queryWrapper);
        //将查询列表结果设置到records中
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public boolean removeAdImageById(String id) {
        //通过id进行查询
        Ad ad = baseMapper.selectById(id);
        if(ad != null) {
            String imagesUrl = ad.getImageUrl();
            if(!StringUtils.isEmpty(imagesUrl)){
                //删除图片
                R r = ossFileService.remove(imagesUrl);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public List<Ad> selectListById(String typeId) {
        QueryWrapper<Ad> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        queryWrapper.eq("type_id", typeId);
        return baseMapper.selectList(queryWrapper);
    }
}
