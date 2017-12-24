package sample.employee;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
@Slf4j
class EmployeeInitializer {
    private static final int NUMBER_TO_GENERATE = 5_000;
    private EmployeeRepository employeeRepository;

    public EmployeeInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostConstruct
    public void init() {
        log.info("generating {} random employees", NUMBER_TO_GENERATE);
        Random randomGenerator = new Random();
        Faker faker = new Faker();
        for (int i = 1; i <= NUMBER_TO_GENERATE; i++) {
            Employee employee = Employee.builder()
                    .id(i)
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .position(faker.job().position())
                    .age(randomGenerator.nextInt(50) + 20)
                    .salary(randomGenerator.nextInt(20000) * 50)
                    .office(OFFICES[randomGenerator.nextInt(OFFICES.length)])
                    .build();
            employeeRepository.save(employee);
        }
    }

    private static final Office[] OFFICES = new Office[] {
            Office.builder().city("Tokyo").build(),
            Office.builder().city("London").build(),
            Office.builder().city("San Francisco").build(),
            Office.builder().city("New York").build(),
            Office.builder().city("Edinburgh").build(),
            Office.builder().city("Sidney").build(),
            Office.builder().city("Singapore").build(),
            null
    };

}