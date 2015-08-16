package sample.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

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

import com.fasterxml.jackson.annotation.JsonView;

/**
 * REST Controller returning {@link DataTablesOutput}
 * 
 * @author Damien Arrachequesne
 */
@RestController
public class UserRestController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HomeRepository homeRepository;

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
			user.setRole(UserRole.values()[i % 3]);
			user.setStatus(UserStatus.values()[i % 2]);
			if (i > 3)
				user.setHome(homes.get(i % 4));
			userRepository.save(user);
		}
	}
}
