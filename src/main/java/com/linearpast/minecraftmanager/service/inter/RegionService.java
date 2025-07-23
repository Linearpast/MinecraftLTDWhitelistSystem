package com.linearpast.minecraftmanager.service.inter;

import com.linearpast.minecraftmanager.entity.Region;

import java.util.List;

public interface RegionService {
	List<Region> findRegion(Long code, Long parentCode, Byte level);
}
