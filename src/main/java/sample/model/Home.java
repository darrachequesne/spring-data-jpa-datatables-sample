package sample.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Home {

	@Id
	@GeneratedValue
	private Integer id;

	@JsonView(DataTablesOutput.View.class)
	@Setter
	private String town;

	@OneToMany(mappedBy = "home")
	@Setter
	private List<User> inhabitants;

}