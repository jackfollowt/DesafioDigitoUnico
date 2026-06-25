package com.inter.desafio.repository;

import com.inter.desafio.model.Calculo;
import com.inter.desafio.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fase 3 - Tarefa 3.1: testes de repositorio mapeando o relacionamento entre
 * Usuario e a sua lista de resultados (Calculo).
 *
 * @DataJpaTest sobe apenas a camada JPA com o H2 em memoria e faz rollback ao final
 * de cada teste, isolando a persistencia.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Persiste usuario com lista de calculos (relacionamento OneToMany em cascata)")
    void persisteUsuarioComCalculos() {
        User user = new User("nome-cifrado", "email-cifrado");
        user.adicionarCalculo(new Calculo("9875", 4, 8));
        user.adicionarCalculo(new Calculo("123456", 7, 3));

        User salvo = userRepository.save(user);

        // garante que a escrita foi para o banco e limpa o contexto de persistencia
        entityManager.flush();
        entityManager.clear();

        Optional<User> recuperado = userRepository.findById(salvo.getId());
        assertTrue(recuperado.isPresent());

        List<Calculo> calculos = recuperado.get().getCalculos();
        assertEquals(2, calculos.size(), "Os dois calculos devem ter sido persistidos em cascata");
        // o id foi gerado pelo banco
        assertNotNull(calculos.get(0).getId());
        // o lado dono (FK usuario_id) aponta de volta para o usuario
        assertEquals(salvo.getId(), calculos.get(0).getUser().getId());
    }

    @Test
    @DisplayName("orphanRemoval: remover um calculo da lista o apaga do banco")
    void orphanRemovalApagaCalculo() {
        User user = new User("nome", "email");
        user.adicionarCalculo(new Calculo("5", 2, 1));
        user.adicionarCalculo(new Calculo("9", 9, 9));
        User salvo = userRepository.save(user);
        entityManager.flush();

        // remove um calculo da colecao e re-salva
        salvo.getCalculos().remove(0);
        userRepository.save(salvo);
        entityManager.flush();
        entityManager.clear();

        User recuperado = userRepository.findById(salvo.getId()).orElseThrow();
        assertEquals(1, recuperado.getCalculos().size());
    }

    @Test
    @DisplayName("Deletar usuario remove seus calculos em cascata")
    void deletarUsuarioRemoveCalculosEmCascata() {
        User user = new User("nome", "email");
        user.adicionarCalculo(new Calculo("1", 10, 1));
        User salvo = userRepository.save(user);
        entityManager.flush();
        Long id = salvo.getId();

        userRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertFalse(userRepository.findById(id).isPresent());
        // a tabela de calculos nao deve conter registros orfaos
        Long totalCalculos = entityManager.getEntityManager()
                .createQuery("select count(c) from Calculo c", Long.class)
                .getSingleResult();
        assertEquals(0L, totalCalculos);
    }
}
