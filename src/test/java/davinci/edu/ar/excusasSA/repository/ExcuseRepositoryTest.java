package davinci.edu.ar.excusasSA.repository;

import davinci.edu.ar.excusasSA.model.Excuse;
import davinci.edu.ar.excusasSA.model.inchargers.Receptionist;
import davinci.edu.ar.excusasSA.model.typeExcuse.TrivialExcuse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ExcuseRepositoryTest {

    @Autowired
    private ExcuseRepository excuseRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Debe guardar una excusa con empleado y tipo")
    void shouldSaveCompleteExcuse(){
        Receptionist receptionist = new Receptionist("Test User", "test@mail.com", 999L, new Normal());
        Excuse excuse = new Excuse(receptionist, new TrivialExcuse());
        Excuse saved = excuseRepository.save(excuse);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmployee().getLegajo()).isEqualTo(999L);
        assertThat(saved.getTypeExcuse()).isInstanceOf(TrivialExcuse.class);


    }
}
