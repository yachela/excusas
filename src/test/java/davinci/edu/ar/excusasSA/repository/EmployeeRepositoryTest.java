package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.Employee;
import davinci.edu.ar.excusasSA.model.inchargers.Receptionist;
import davinci.edu.ar.excusasSA.model.strategy.Normal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Debe persistir un Recepcionista correctamente")
    void shouldSaveEmployee() {
        Receptionist receptionist = new Receptionist("Kevinha", "kevinha@algo.com", 1234L, new Normal());

        Employee saved = employeeRepository.save(receptionist);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Kevinha");
        assertThat(saved.getLegajo()).isEqualTo(1234);
    }
}