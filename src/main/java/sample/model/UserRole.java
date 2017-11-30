package sample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
	ADMIN("admin"), AUTHOR("author"), USER("user");

	private String role;

}