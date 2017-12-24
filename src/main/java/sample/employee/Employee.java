package sample.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {
    @Id private int id;
    private String firstName;
    private String lastName;
    private String position;
    private int age;
    private int salary;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_office")
    private Office office;

}