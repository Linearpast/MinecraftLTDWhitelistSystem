package com.linearpast.minecraftmanager.controller;

import com.linearpast.minecraftmanager.entity.Region;
import com.linearpast.minecraftmanager.entity.dto.RegionFindDTO;
import com.linearpast.minecraftmanager.service.RegionService;
import com.linearpast.minecraftmanager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/region")
public class RegionController {

	@Autowired
	private RegionService regionService;

	@PostMapping("/findRegion")
	public Result<?> findRegion(@RequestBody RegionFindDTO regionFindDTO) {
		List<Region> region = regionService.findRegion(
				regionFindDTO.getCode(),
				regionFindDTO.getParentCode(),
				regionFindDTO.getLevel()
		);
		return Result.success(region);
	}
}
