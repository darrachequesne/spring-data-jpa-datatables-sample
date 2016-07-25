package sample.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Sample model
 * 
 * @author Damien Arrachequesne
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue
	@JsonView(DataTablesOutput.View.class)
	private Integer id;

	@JsonView(DataTablesOutput.View.class)
	private String mail;

	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@JsonView(DataTablesOutput.View.class)
	private UserStatus status;

	@ManyToOne
	@JoinColumn(name = "id_home")
	@JsonView(DataTablesOutput.View.class)
	private Home home;

}
