package sample.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Sample model
 * 
 * @author Damien Arrachequesne
 */
@Entity
public class User {

	@Id
	@GeneratedValue
	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private Integer id;

	@Getter
	@Setter
	@JsonView(DataTablesOutput.View.class)
	private String mail;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private UserRole role;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private UserStatus status;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "id_home")
	@JsonView(DataTablesOutput.View.class)
	private Home home;

	public User() {
		super();
	}

}
