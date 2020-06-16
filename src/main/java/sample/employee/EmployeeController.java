package sample.employee;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.validation.Valid;

import static org.springframework.util.StringUtils.hasText;

@RestController
public class EmployeeController {
    private EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public DataTablesOutput<Employee> list(@Valid DataTablesInput input) {
        return employeeRepository.findAll(input);
    }

    @RequestMapping(value = "/employees", method = RequestMethod.POST)
    public DataTablesOutput<Employee> listPOST(@Valid @RequestBody DataTablesInput input) {
        return employeeRepository.findAll(input);
    }

    @RequestMapping(value = "/employees-advanced", method = RequestMethod.GET)
    public DataTablesOutput<Employee> listAdvanced(@Valid DataTablesInput input) {
        return employeeRepository.findAll(input, new SalarySpecification(input), new ExcludeAnalystsSpecification());
    }

    @RequestMapping(value = "/employees-rendered-column", method = RequestMethod.GET)
    public DataTablesOutput<Employee> listRendered(@Valid DataTablesInput input) {
        String searchValue = escapeContent(input.getSearch().getValue());
        input.getSearch().setValue(""); // prevent search on other fields

        Specification<Employee> fullNameSpecification = (Specification<Employee>) (root, query, criteriaBuilder) -> {
            if (!hasText(searchValue)) {
                return null;
            }
            String[] parts = searchValue.split(" ");
            Expression<String> firstNameExpression = criteriaBuilder.lower(root.get("firstName"));
            Expression<String> lastNameExpression = criteriaBuilder.lower(root.get("lastName"));
            if (parts.length == 2 && hasText(parts[0]) && hasText(parts[1])) {
                return criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(firstNameExpression, parts[0]),
                                criteriaBuilder.like(lastNameExpression, parts[1] + "%", '~')
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.equal(lastNameExpression, parts[0]),
                                criteriaBuilder.like(firstNameExpression, parts[1] + "%", '~')
                        )
                );
            } else {
                return criteriaBuilder.or(
                        criteriaBuilder.like(firstNameExpression, searchValue + "%", '~'),
                        criteriaBuilder.like(lastNameExpression, searchValue + "%", '~')
                );
            }
        };
        return employeeRepository.findAll(input, fullNameSpecification);
    }

    private String escapeContent(String content) {
        return content
                .replaceAll("~", "~~")
                .replaceAll("%", "~%")
                .replaceAll("_", "~_")
                .trim()
                .toLowerCase();
    }

    private static class SalarySpecification implements Specification<Employee> {
        private final Integer minSalary;
        private final Integer maxSalary;

        SalarySpecification(DataTablesInput input) {
            String salaryFilter = input.getColumn("salary").getSearch().getValue();
            if (!hasText(salaryFilter)) {
                minSalary = maxSalary = null;
                return;
            }
            String[] bounds = salaryFilter.split(";");
            minSalary = getValue(bounds, 0);
            maxSalary = getValue(bounds, 1);
        }

        private Integer getValue(String[] bounds, int index) {
            if (bounds.length > index && hasText(bounds[index])) {
                try {
                    return Integer.valueOf(bounds[index]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Expression<Integer> salary = root.get("salary").as(Integer.class);
            if (minSalary != null && maxSalary != null) {
                return criteriaBuilder.between(salary, minSalary, maxSalary);
            } else if (minSalary != null) {
                return criteriaBuilder.greaterThanOrEqualTo(salary, minSalary);
            } else if (maxSalary != null) {
                return criteriaBuilder.lessThanOrEqualTo(salary, maxSalary);
            } else {
                return criteriaBuilder.conjunction();
            }
        }
    }

    private static class ExcludeAnalystsSpecification implements Specification<Employee> {
        @Override
        public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.notEqual(root.get("position"), "Analyst");
        }
    }

}