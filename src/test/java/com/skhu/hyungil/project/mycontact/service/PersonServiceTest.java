package com.skhu.hyungil.project.mycontact.service;

import com.skhu.hyungil.project.mycontact.controller.dto.PersonDto;
import com.skhu.hyungil.project.mycontact.domain.Person;
import com.skhu.hyungil.project.mycontact.domain.dto.Birthday;
import com.skhu.hyungil.project.mycontact.repository.PersonRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    // PersonRepository로 Mock를 만들어서 PersonService에 주입
    @InjectMocks // 테스트의 대상이 되는 클래스
    private PersonService personService;
    @Mock // 해당대상이 되는 클래스에서 오토와일드 하고 있는 것들
    private PersonRepository personRepository;

    @Test
    void getPeopleByName() {
        when(personRepository.findByName("hyungil"))  // when은 if와 같은 느낌
                .thenReturn(Lists.newArrayList(new Person("hyungil")));

        List<Person> result = personService.getPeopleByName("hyungil");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("hyungil");
    }

    @Test
    void getPerson() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("hyungil")));

        Person person = personService.getPerson(1L);

        assertThat(person.getName()).isEqualTo("hyungil");
    }

    @Test
    void getPersonIfNotFound() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.empty());

        Person person = personService.getPerson(1L);

        assertThat(person).isNull();
    }

    @Test
    void put() {
        personService.put(mockPersonDto());

        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void modifyIfPersonNotFound() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> personService.modify(1L, mockPersonDto()));
    }

    @Test
    void modifyIfNameIsDifferent() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("jihun")));

        assertThrows(RuntimeException.class, () -> personService.modify(1L, mockPersonDto()));
    }

    @Test
    void modify() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("hyungil")));

        personService.modify(1L, mockPersonDto());

        // verify(personRepository, times(1)).save(any(Person.class));
        verify(personRepository, times(1)).save(argThat(new IsPersonWillBeUpdated()));
    }

    @Test
    void modifyByNameIfPersonNotFound() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> personService.modify(1L, "suhun"));
    }

    @Test
    void modifyByName() {
        when(personRepository.findById(1L))
                .thenReturn(Optional.of(new Person("hyungil")));

        personService.modify(1L, "suhun");

        verify(personRepository, times(1)).save((argThat(new IsNameWillBeUpdated())));
    }

    private static class IsPersonWillBeUpdated implements ArgumentMatcher<Person> {
        @Override
        public boolean matches(Person person) {
            return equals(person.getName(), "hyungil")
                    && equals(person.getHobby(), "programming")
                    && equals(person.getAddress(), "안양")
                    && equals(person.getBirthday(), Birthday.of(LocalDate.now()))
                    && equals(person.getJob(), "programmer")
                    && equals(person.getPhoneNumber(), "010-1234-1234");
        }

        private boolean equals(Object actual, Object expected) {
            return expected.equals(actual);
        }

    }

    private PersonDto mockPersonDto() {
        return PersonDto.of("hyungil", "programming", "안양", LocalDate.now(), "programmer", "010-1234-1234");
    }

    private static class IsNameWillBeUpdated implements ArgumentMatcher<Person> {
        @Override
        public boolean matches(Person person) {
            return person.getName().equals("suhun");
        }
    }
}