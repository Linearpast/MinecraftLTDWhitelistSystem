package com.linearpast.minecraftmanager.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Data
@Table(name = "player_info_view")
public class PlayerInfoView {
	@Id
	@Column(name = "player_id")
	private Integer id;
	private String description;
	private String uuid;
	private Byte status;
	private String qq;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "region_code")
	private Long regionCode;

	@Column(name = "operator_id")
	private Integer operatorId;

	@Column(name = "player_name")
	private String playerName;

	@Column(name = "region_full_name")
	private String regionFullName;

	@Column(name = "operator_username")
	private String operatorUsername;

	@Column(name = "operator_nickname")
	private String operatorNickname;

	@Column(name = "total_score")
	private Integer totalScore;

	@Column(name = "email_active")
	private Boolean emailActive;
}
