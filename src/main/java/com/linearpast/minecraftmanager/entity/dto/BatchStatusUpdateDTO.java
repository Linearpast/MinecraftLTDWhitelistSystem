package com.linearpast.minecraftmanager.entity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchStatusUpdateDTO {
	private List<Integer> ids;
	private Byte status;
}
