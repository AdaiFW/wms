package com.warehouse.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.common.exception.BusinessException;
import com.warehouse.dto.GoodsDto;
import com.warehouse.entity.Goods;
import com.warehouse.entity.Inventory;
import com.warehouse.mapper.GoodsMapper;
import com.warehouse.mapper.InventoryMapper;
import com.warehouse.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public Page<Goods> page(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Goods::getGoodsName, keyword)
                   .or().like(Goods::getGoodsCode, keyword);
        }
        wrapper.orderByDesc(Goods::getCreateTime);
        return goodsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Goods getById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    @Transactional
    public Goods create(GoodsDto dto) {
        Goods goods = new Goods();
        goods.setGoodsCode(dto.getGoodsCode());
        goods.setGoodsName(dto.getGoodsName());
        goods.setCategoryId(dto.getCategoryId());
        goods.setUnit(dto.getUnit());
        goods.setSpec(dto.getSpec());
        goods.setPrice(dto.getPrice());
        goods.setImageUrl(dto.getImageUrl());
        goods.setDescription(dto.getDescription());
        goodsMapper.insert(goods);

        Inventory inv = new Inventory();
        inv.setGoodsId(goods.getId());
        inv.setQuantity(0);
        inv.setMinStock(0);
        inv.setMaxStock(99999);
        inv.setAlertThreshold(10);
        inventoryMapper.insert(inv);

        return goods;
    }

    @Override
    public Goods update(Long id, GoodsDto dto) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) throw new BusinessException("商品不存在");
        goods.setGoodsCode(dto.getGoodsCode());
        goods.setGoodsName(dto.getGoodsName());
        goods.setCategoryId(dto.getCategoryId());
        goods.setUnit(dto.getUnit());
        goods.setSpec(dto.getSpec());
        goods.setPrice(dto.getPrice());
        goods.setImageUrl(dto.getImageUrl());
        goods.setDescription(dto.getDescription());
        goodsMapper.updateById(goods);
        return goods;
    }

    @Override
    public void delete(Long id) {
        goodsMapper.deleteById(id);
    }
}
