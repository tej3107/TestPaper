package com.testpaper.demo.resources;

import com.testpaper.demo.model.Person;
import com.testpaper.demo.repository.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/people")
public class PersonResource {

	private final PersonRepository personRepository;

	public PersonResource(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@PostMapping
	public ResponseEntity<Person> create(@RequestBody Person person) {
		Person saved = personRepository.save(person);
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Person> getById(@PathVariable Long id) {
		Optional<Person> found = personRepository.findById(id);
		return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}