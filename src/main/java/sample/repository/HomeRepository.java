package sample.repository;

import org.springframework.data.repository.CrudRepository;

import sample.model.Home;

public interface HomeRepository extends CrudRepository<Home, Integer> {

}
