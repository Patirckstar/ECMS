package com.ecms_backend.service;

import com.ecms_backend.entity.*;
import com.ecms_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SkuService {

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Autowired
    private PriceLogMapper priceLogMapper;

    public List<Sku> getBySpuId(Long spuId) {
        return skuMapper.selectBySpuId(spuId);
    }

    @Transactional
    public Sku update(Long spuId, Long skuId, Sku sku) {
        Sku oldSku = skuMapper.selectById(skuId);
        if (oldSku != null && oldSku.getSalePrice() != null
                && sku.getSalePrice() != null
                && oldSku.getSalePrice().compareTo(sku.getSalePrice()) != 0) {
            PriceLog priceLog = new PriceLog();
            priceLog.setSkuId(skuId);
            priceLog.setPriceType("sale_price");
            priceLog.setBeforePrice(oldSku.getSalePrice());
            priceLog.setAfterPrice(sku.getSalePrice());
            priceLog.setOperatorId(null);
            priceLog.setCreatedAt(LocalDateTime.now());
            priceLogMapper.insert(priceLog);
        }

        sku.setId(skuId);
        sku.setSpuId(spuId);
        sku.setUpdatedAt(LocalDateTime.now());
        skuMapper.update(sku);
        return sku;
    }

    @Transactional
    public void batchUpdate(Long spuId, List<Sku> skus) {
        for (Sku sku : skus) {
            sku.setSpuId(spuId);
            if (sku.getId() != null) {
                Sku oldSku = skuMapper.selectById(sku.getId());
                if (oldSku != null && oldSku.getSalePrice() != null
                        && sku.getSalePrice() != null
                        && oldSku.getSalePrice().compareTo(sku.getSalePrice()) != 0) {
                    PriceLog priceLog = new PriceLog();
                    priceLog.setSkuId(sku.getId());
                    priceLog.setPriceType("sale_price");
                    priceLog.setBeforePrice(oldSku.getSalePrice());
                    priceLog.setAfterPrice(sku.getSalePrice());
                    priceLog.setOperatorId(null);
                    priceLog.setCreatedAt(LocalDateTime.now());
                    priceLogMapper.insert(priceLog);
                }
            }
        }
        skuMapper.batchUpdate(skus);
    }

    @Transactional
    public void adjustStock(Long spuId, Long skuId, int qty, String remark) {
        Sku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            return;
        }

        int beforeStock = sku.getStock() != null ? sku.getStock() : 0;
        int afterStock = beforeStock + qty;

        sku.setStock(afterStock);
        sku.setUpdatedAt(LocalDateTime.now());
        skuMapper.update(sku);

        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setSkuId(skuId);
        inventoryLog.setSpuId(spuId);
        inventoryLog.setChangeType(qty > 0 ? 1 : 2);
        inventoryLog.setChangeQty(Math.abs(qty));
        inventoryLog.setBeforeStock(beforeStock);
        inventoryLog.setAfterStock(afterStock);
        inventoryLog.setOperatorId(null);
        inventoryLog.setRemark(remark);
        inventoryLog.setCreatedAt(LocalDateTime.now());
        inventoryLogMapper.insert(inventoryLog);
    }

    @Transactional
    public void batchAdjustStock(Long spuId, List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            Long skuId = Long.valueOf(item.get("skuId").toString());
            int qty = Integer.parseInt(item.get("qty").toString());
            String remark = item.get("remark") != null ? item.get("remark").toString() : null;
            adjustStock(spuId, skuId, qty, remark);
        }
    }
}
