package sample.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import sample.model.User;

/**
 * User repository extending {@link DataTablesRepository}
 * 
 * @author Damien Arrachequesne
 */
public interface UserRepository extends DataTablesRepository<User, Integer> {

}
