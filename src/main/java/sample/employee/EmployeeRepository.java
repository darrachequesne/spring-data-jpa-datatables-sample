package sample.employee;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface EmployeeRepository extends DataTablesRepository<Employee, Integer> {

}
