package sample.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.data.jpa.datatables.mapping.SearchPanes;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

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
        SalarySpecification salarySpecification = new SalarySpecification(input);
        FirstDaySpecification firstDaySpecification = new FirstDaySpecification(input);
        return employeeRepository.findAll(input, salarySpecification.and(firstDaySpecification), new ExcludeAnalystsSpecification());
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

    private static class FirstDaySpecification implements Specification<Employee> {
        private final LocalDate minFirstDay;
        private final LocalDate maxFirstDay;

        FirstDaySpecification(DataTablesInput input) {
            Search columnSearch = input.getColumn("firstDay").getSearch();
            String dateFilter = columnSearch.getValue();
            columnSearch.setValue("");
            if (!hasText(dateFilter)) {
                minFirstDay = maxFirstDay = null;
                return;
            }
            String[] bounds = dateFilter.split(";");
            minFirstDay = getValue(bounds, 0);
            maxFirstDay = getValue(bounds, 1);
        }

        private LocalDate getValue(String[] bounds, int index) {
            if (bounds.length > index && hasText(bounds[index])) {
                try {
                    return LocalDate.parse(bounds[index]);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            Expression<LocalDate> firstDay = root.get("firstDay").as(LocalDate.class);
            if (minFirstDay != null && maxFirstDay != null) {
                return criteriaBuilder.between(firstDay, minFirstDay, maxFirstDay);
            } else if (minFirstDay != null) {
                return criteriaBuilder.greaterThanOrEqualTo(firstDay, minFirstDay);
            } else if (maxFirstDay != null) {
                return criteriaBuilder.lessThanOrEqualTo(firstDay, maxFirstDay);
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

    @RequestMapping(value = "/employees-searchpanes-basic", method = RequestMethod.GET)
    public DataTablesOutput<Employee> listWithBasicSearchPanes(@Valid DataTablesInput input, @RequestParam Map<String, String> queryParameters) {
        input.parseSearchPanesFromQueryParams(queryParameters, Arrays.asList("position", "age"));
        return employeeRepository.findAll(input);
    }

    @RequestMapping(value = "/employees-searchpanes-range", method = RequestMethod.GET)
    public DataTablesOutput<Employee> listWithAdvancedSearchPanes(@Valid DataTablesInput input, @RequestParam Map<String, String> queryParameters) {
        input.parseSearchPanesFromQueryParams(queryParameters, Arrays.asList("position", "salary"));

        Set<String> salaryRanges = input.getSearchPanes().remove("salary");
        Specification<Employee> salarySpecification = createSalarySpecification(salaryRanges);

        DataTablesOutput<Employee> output = employeeRepository.findAll(input, salarySpecification);
        output.getSearchPanes().getOptions().put("salary", computeSalaryRanges());

        return output;
    }

    private Specification<Employee> createSalarySpecification(Set<String> salaryRanges) {
        if (salaryRanges.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            salaryRanges.forEach(range -> {
                Path<Long> salary = root.get("salary");
                switch (range) {
                    case "< 300 000":
                        predicates.add(criteriaBuilder.lessThan(salary, 300_000L));
                        break;
                    case "300 000 <= ... < 500 000":
                        predicates.add(
                                criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(salary, 300_000L),
                                        criteriaBuilder.lessThan(salary, 500_000L)
                                )
                        );
                        break;
                    case "500 000 <= ... < 700 000":
                        predicates.add(
                                criteriaBuilder.and(
                                        criteriaBuilder.greaterThanOrEqualTo(salary, 500_000L),
                                        criteriaBuilder.lessThan(salary, 700_000L)
                                )
                        );
                        break;
                    case ">= 700 000":
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(salary, 700_000L));
                        break;
                }
            });
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private List<SearchPanes.Item> computeSalaryRanges() {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);

        Root<Employee> root = query.from(Employee.class);
        Expression<Long> salary = root.get("salary").as(Long.class);

        Expression<Object> salaryRange = criteriaBuilder.selectCase()
                .when(criteriaBuilder.lessThan(salary, 300_000L), "< 300 000")
                .when(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(salary, 300_000L),
                        criteriaBuilder.lessThan(salary, 500_000L)
                ), "300 000 <= ... < 500 000")
                .when(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(salary, 500_000L),
                        criteriaBuilder.lessThan(salary, 700_000L)
                ), "500 000 <= ... < 700 000")
                .when(criteriaBuilder.greaterThanOrEqualTo(salary, 700_000L), ">= 700 000");

        query.multiselect(salaryRange, criteriaBuilder.count(root));
        query.groupBy(salaryRange);

        List<SearchPanes.Item> items = new ArrayList<>();

        this.entityManager.createQuery(query).getResultList().forEach(objects -> {
            String value = String.valueOf(objects[0]);
            long count = (long) objects[1];
            items.add(new SearchPanes.Item(value, value, count, count));
        });

        return items;
    }

}