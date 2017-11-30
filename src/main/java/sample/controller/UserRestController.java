package sample.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sample.model.Home;
import sample.model.User;
import sample.model.UserRole;
import sample.model.UserStatus;
import sample.repository.HomeRepository;
import sample.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller returning {@link DataTablesOutput}
 * 
 * @author Damien Arrachequesne
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserRestController {

	private final UserRepository userRepository;
	private final HomeRepository homeRepository;

	@JsonView(DataTablesOutput.View.class)
	@RequestMapping(value = "/data/users", method = RequestMethod.GET)
	public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
		return userRepository.findAll(input);
	}

	@PostConstruct
	public void insertSampleData() {
		List<Home> homes = new ArrayList<Home>();
		for (int i = 0; i < 4; i++) {
			Home home = new Home();
			home.setTown("town" + i);
			home = homeRepository.save(home);
			homes.add(home);
		}
		for (int i = 0; i < 42; i++) {
			User user = new User();
			user.setMail("john" + i + "@doe.com");
			user.setRole(UserRole.values()[i % UserRole.values().length]);
			user.setStatus(UserStatus.values()[i % UserStatus.values().length]);
			if (i > 3) {
				user.setHome(homes.get(i % homes.size()));
			}
			userRepository.save(user);
		}
	}
}
