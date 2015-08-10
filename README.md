# spring-data-jpa-datatables-sample
This project is a sample Spring Boot project using https://github.com/darrachequesne/spring-data-jpa-datatables

## How to run

```
mvn spring-boot:run
```

Then open [http://localhost:8080/](http://localhost:8080/)

## Features

A DataTable is defined in the file home.js with columns' name matching attributes of the User class:

```
$(document).ready(function() {
	var table = $('table#sample').DataTable({
		'ajax' : '/data/users',
		'serverSide' : true,
		columns : [ {
			data : 'id'
		}, {
			data : 'mail'
		}, {
			data : 'role'
		}, {
			data : 'status'
		} ]
	});
});
```

The UserRestController handles the Ajax requests sent by DataTables plugin

```
@RestController
public class UserRestController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/data/users", method = RequestMethod.GET)
	public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
		return userRepository.findAll(input);
	}
}
```

It performs the required processing (paging, ordering, searching, etc.) and then return the data in the format required by DataTables.

A global search input is already available, and some selectors have been added to perform per-column filtering:
```
$('select#role_selector').change(function() {
	var filter = '';
	$('select#role_selector option:selected').each(function() {
		filter += $(this).text() + "+";
	});
	filter = filter.substring(0, filter.length - 1);
	table.columns(2).search(filter).draw();
});
```
