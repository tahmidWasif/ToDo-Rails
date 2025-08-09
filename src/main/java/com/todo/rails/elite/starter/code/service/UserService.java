package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// add a user
	public User addUser(@NotNull(message = "User cannot be null") User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}
		String password = user.getPassword();
		// TODO 7: enable password encoder in user service
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	// get a user by username
	public User getUserByUsername(
			@NotNull(message = "Username cannot be null")
			@NotBlank(message = "Username cannot be blank")
			String username
	) {
		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new RuntimeException("User not found")
				);
	}

	// get a user by email
	public User getUserByEmail(
			@NotNull(message = "Email cannot be null")
			@NotBlank(message = "Email cannot be blank")
			@Email(message = "Email should be valid")
			String email
	) {
		return userRepository.findByEmail(email)
				.orElseThrow(
						() -> new RuntimeException("User not found")
				);
	}

	// get a user by id
	public User getUserById(
			@NotNull(message = "Id cannot be null")
			Long id
	) {
		return userRepository.findById(id)
				.orElseThrow(
						() -> new RuntimeException("User not found")
				);
	}

	// update a user
	public User updateUser(@NotNull(message = "User cannot be null") User user) {
		if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
			throw new RuntimeException("User not found");
		}
		return userRepository.save(user);
	}

	// delete a user
	public void deleteUser(@NotNull(message = "User cannot be null") User user) {
		if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
			throw new RuntimeException("User not found");
		}
		userRepository.delete(user);
	}

	public List<User> getAllUsers() {
		if (userRepository.findAll().isEmpty()) {
			throw new RuntimeException("No users found");
		}
		return userRepository.findAll();
	}
}
