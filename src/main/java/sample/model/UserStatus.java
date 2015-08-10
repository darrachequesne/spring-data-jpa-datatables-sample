package sample.model;

import lombok.Getter;

public enum UserStatus {
	ACTIVE("active"), BLOCKED("blocked");

	@Getter
	private String status;

	private UserStatus(String status) {
		this.status = status;
	}
}