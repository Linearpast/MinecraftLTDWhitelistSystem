package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionFindDTO {
	private Long code;
	private Long parentCode;
	private Byte level;
}
