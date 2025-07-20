package com.linearpast.minecraftmanager.service.impl;

import com.linearpast.minecraftmanager.entity.Region;
import com.linearpast.minecraftmanager.repository.RegionRepository;
import com.linearpast.minecraftmanager.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {
	@Autowired
	private RegionRepository regionRepository;

	@Override
	public List<Region> findRegion(Long code, Long parentCode, Byte level) {
		Specification<Region> spec = RegionRepository.RegionSpecifications
				.withDynamicQuery(code, parentCode, level);
		return regionRepository.findAll(spec);
	}


}
