package sample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
	ACTIVE("active"), BLOCKED("blocked");

	@Getter
	private String status;

}