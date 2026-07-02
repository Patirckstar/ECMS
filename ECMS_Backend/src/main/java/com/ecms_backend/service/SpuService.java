package com.ecms_backend.service;

import com.ecms_backend.common.PageResult;
import com.ecms_backend.entity.*;
import com.ecms_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpuService {

	private static final int STATUS_DRAFT = 0;
	private static final int STATUS_PENDING_AUDIT = 1;
	private static final int STATUS_ON_SHELF = 2;
	private static final int STATUS_OFF_SHELF = 3;
	private static final int STATUS_REJECTED = 4;

	private static final int AUDIT_RESULT_APPROVED = 1;
	private static final int AUDIT_RESULT_REJECTED = 2;

	private static final int AUDIT_STATUS_PENDING = 0;
	private static final int AUDIT_STATUS_APPROVED = 1;
	private static final int AUDIT_STATUS_REJECTED = 2;

	@Autowired
	private SpuMapper spuMapper;

	@Autowired
	private SkuMapper skuMapper;

	@Autowired
	private SpuImageMapper spuImageMapper;

	@Autowired
	private SpuTagMapper spuTagMapper;

	@Autowired
	private TagMapper tagMapper;

	@Autowired
	private StatusLogMapper statusLogMapper;

	@Autowired
	private InventoryLogMapper inventoryLogMapper;

	@Autowired
	private AuditLogMapper auditLogMapper;

	public PageResult<Spu> queryPage(Map<String, Object> params) {
		int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
		int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
		int offset = (page - 1) * pageSize;

		params.put("offset", offset);
		params.put("limit", pageSize);

		List<Spu> list = spuMapper.selectList(params);
		long total = spuMapper.countList(params);

		return PageResult.of(list, total, page, pageSize);
	}

	public Spu getById(Long id) {
		Spu spu = spuMapper.selectById(id);
		if (spu != null) {
			spu.setImages(spuImageMapper.selectBySpuId(id));

			List<SpuTag> spuTags = spuTagMapper.selectBySpuId(id);
			if (spuTags != null && !spuTags.isEmpty()) {
				List<Long> tagIds = spuTags.stream()
						.map(SpuTag::getTagId)
						.collect(Collectors.toList());
				List<Tag> tags = tagMapper.selectAll().stream()
						.filter(t -> tagIds.contains(t.getId()))
						.collect(Collectors.toList());
				spu.setTags(tags);
			}
		}
		return spu;
	}

	@Transactional
	public Spu create(Spu spu) {
		spu.setSpuCode("SPU" + System.currentTimeMillis());
		spu.setStatus(STATUS_DRAFT);
		spu.setIsDeleted(0);
		spu.setCreatedBy(spu.getCreatedBy() != null ? spu.getCreatedBy() : 1L);
		spu.setCreatedAt(LocalDateTime.now());
		spu.setUpdatedAt(LocalDateTime.now());
		spuMapper.insert(spu);

		if (spu.getSkus() == null || spu.getSkus().isEmpty()) {
			Sku defaultSku = new Sku();
			defaultSku.setSkuCode(spu.getSpuCode() + "_SKU1");
			defaultSku.setSpuId(spu.getId());
			defaultSku.setSpecInfo("{}");
			defaultSku.setMarketPrice(spu.getMarketPrice() != null ? spu.getMarketPrice() : BigDecimal.ZERO);
			defaultSku.setSalePrice(spu.getSalePrice() != null ? spu.getSalePrice() : BigDecimal.ZERO);
			defaultSku.setMemberPrice(spu.getMemberPrice() != null ? spu.getMemberPrice() : BigDecimal.ZERO);
			defaultSku.setStock(0);
			defaultSku.setLockedStock(0);
			defaultSku.setWarnThreshold(0);
			defaultSku.setStatus(1);
			defaultSku.setCreatedAt(LocalDateTime.now());
			defaultSku.setUpdatedAt(LocalDateTime.now());
			skuMapper.insert(defaultSku);
		} else {
			for (int i = 0; i < spu.getSkus().size(); i++) {
				Sku sku = spu.getSkus().get(i);
				sku.setSkuCode(spu.getSpuCode() + "_SKU" + (i + 1));
				sku.setSpuId(spu.getId());
				sku.setCreatedAt(LocalDateTime.now());
				sku.setUpdatedAt(LocalDateTime.now());
				skuMapper.insert(sku);
			}
		}

		if (spu.getImages() != null && !spu.getImages().isEmpty()) {
			for (SpuImage image : spu.getImages()) {
				image.setSpuId(spu.getId());
				image.setCreatedAt(LocalDateTime.now());
				spuImageMapper.insert(image);
			}
		}

		if (spu.getTagIds() != null && !spu.getTagIds().isEmpty()) {
			for (Long tagId : spu.getTagIds()) {
				SpuTag spuTag = new SpuTag();
				spuTag.setSpuId(spu.getId());
				spuTag.setTagId(tagId);
				spuTag.setCreatedAt(LocalDateTime.now());
				spuTagMapper.insert(spuTag);
			}
		} else if (spu.getTags() != null && !spu.getTags().isEmpty()) {
			for (Tag tag : spu.getTags()) {
				SpuTag spuTag = new SpuTag();
				spuTag.setSpuId(spu.getId());
				spuTag.setTagId(tag.getId());
				spuTag.setCreatedAt(LocalDateTime.now());
				spuTagMapper.insert(spuTag);
			}
		}

		return spu;
	}

	@Transactional
	public Spu update(Long id, Spu spu) {
		spu.setId(id);
		spu.setUpdatedAt(LocalDateTime.now());
		spuMapper.update(spu);

		if (spu.getImages() != null) {
			spuImageMapper.deleteBySpuId(id);
			for (SpuImage image : spu.getImages()) {
				image.setSpuId(id);
				image.setCreatedAt(LocalDateTime.now());
				spuImageMapper.insert(image);
			}
		}

		if (spu.getTagIds() != null) {
			spuTagMapper.deleteBySpuId(id);
			for (Long tagId : spu.getTagIds()) {
				SpuTag spuTag = new SpuTag();
				spuTag.setSpuId(id);
				spuTag.setTagId(tagId);
				spuTag.setCreatedAt(LocalDateTime.now());
				spuTagMapper.insert(spuTag);
			}
		} else if (spu.getTags() != null) {
			spuTagMapper.deleteBySpuId(id);
			for (Tag tag : spu.getTags()) {
				SpuTag spuTag = new SpuTag();
				spuTag.setSpuId(id);
				spuTag.setTagId(tag.getId());
				spuTag.setCreatedAt(LocalDateTime.now());
				spuTagMapper.insert(spuTag);
			}
		}

		if (spu.getMarketPrice() != null || spu.getSalePrice() != null || spu.getMemberPrice() != null) {
			List<Sku> skus = skuMapper.selectBySpuId(id);
			for (Sku sku : skus) {
				if (spu.getMarketPrice() != null) {
					sku.setMarketPrice(spu.getMarketPrice());
				}
				if (spu.getSalePrice() != null) {
					sku.setSalePrice(spu.getSalePrice());
				}
				if (spu.getMemberPrice() != null) {
					sku.setMemberPrice(spu.getMemberPrice());
				}
				sku.setUpdatedAt(LocalDateTime.now());
				skuMapper.update(sku);
			}
		}

		return spu;
	}

	public void delete(Long id) {
		spuMapper.deleteById(id);
	}

	public void onShelf(Long id) {
		Spu spu = spuMapper.selectById(id);
		if (spu == null) {
			throw new RuntimeException("商品不存在");
		}
		int currentStatus = spu.getStatus();
		if (currentStatus != STATUS_PENDING_AUDIT && currentStatus != STATUS_OFF_SHELF) {
			throw new RuntimeException("只有待审核或已下架状态的商品才能上架");
		}
		spuMapper.updateStatus(id, STATUS_ON_SHELF);
		recordStatusLog(id, currentStatus, STATUS_ON_SHELF, null);
	}

	public void offShelf(Long id) {
		Spu spu = spuMapper.selectById(id);
		if (spu == null) {
			throw new RuntimeException("商品不存在");
		}
		int currentStatus = spu.getStatus();
		if (currentStatus != STATUS_ON_SHELF) {
			throw new RuntimeException("只有已上架状态的商品才能下架");
		}
		spuMapper.updateStatus(id, STATUS_OFF_SHELF);
		recordStatusLog(id, currentStatus, STATUS_OFF_SHELF, null);
	}

	@Transactional
	public int batchOnShelf(List<Long> ids) {
		int successCount = 0;
		for (Long id : ids) {
			try {
				Spu spu = spuMapper.selectById(id);
				if (spu == null) {
					continue;
				}
				int currentStatus = spu.getStatus();
				if (currentStatus != STATUS_PENDING_AUDIT && currentStatus != STATUS_OFF_SHELF) {
					continue;
				}
				spuMapper.updateStatus(id, STATUS_ON_SHELF);
				recordStatusLog(id, currentStatus, STATUS_ON_SHELF, null);
				successCount++;
			} catch (Exception e) {
				continue;
			}
		}
		return successCount;
	}

	@Transactional
	public int batchOffShelf(List<Long> ids) {
		int successCount = 0;
		for (Long id : ids) {
			try {
				Spu spu = spuMapper.selectById(id);
				if (spu == null) {
					continue;
				}
				int currentStatus = spu.getStatus();
				if (currentStatus != STATUS_ON_SHELF) {
					continue;
				}
				spuMapper.updateStatus(id, STATUS_OFF_SHELF);
				recordStatusLog(id, currentStatus, STATUS_OFF_SHELF, null);
				successCount++;
			} catch (Exception e) {
				continue;
			}
		}
		return successCount;
	}

	@Transactional
	public void batchDelete(List<Long> ids) {
		for (Long id : ids) {
			spuMapper.deleteById(id);
		}
	}

	public void submitAudit(Long id) {
		spuMapper.updateStatus(id, STATUS_PENDING_AUDIT);
	}

	public void approve(Long id, Long auditorId) {
		Spu spu = spuMapper.selectById(id);
		if (spu != null) {
			LocalDateTime now = LocalDateTime.now();
			spuMapper.updateStatus(id, STATUS_ON_SHELF);
			spuMapper.updateAuditInfo(id, AUDIT_STATUS_APPROVED, auditorId, now, null);

			AuditLog auditLog = new AuditLog();
			auditLog.setSpuId(id);
			auditLog.setAuditResult(AUDIT_RESULT_APPROVED);
			auditLog.setAuditorId(auditorId);
			auditLog.setAuditTime(now);
			auditLogMapper.insert(auditLog);
		}
	}

	public void reject(Long id, Long auditorId, String reason) {
		Spu spu = spuMapper.selectById(id);
		if (spu != null) {
			LocalDateTime now = LocalDateTime.now();
			spuMapper.updateStatus(id, STATUS_REJECTED);
			spuMapper.updateAuditInfo(id, AUDIT_STATUS_REJECTED, auditorId, now, reason);

			AuditLog auditLog = new AuditLog();
			auditLog.setSpuId(id);
			auditLog.setAuditResult(AUDIT_RESULT_REJECTED);
			auditLog.setAuditorId(auditorId);
			auditLog.setRejectReason(reason);
			auditLog.setAuditTime(now);
			auditLogMapper.insert(auditLog);
		}
	}

	private void recordStatusLog(Long spuId, Integer fromStatus, Integer toStatus, String remark) {
		StatusLog statusLog = new StatusLog();
		statusLog.setSpuId(spuId);
		statusLog.setFromStatus(fromStatus);
		statusLog.setToStatus(toStatus);
		statusLog.setOperatorId(null);
		statusLog.setRemark(remark);
		statusLog.setCreatedAt(LocalDateTime.now());
		statusLogMapper.insert(statusLog);
	}
}
