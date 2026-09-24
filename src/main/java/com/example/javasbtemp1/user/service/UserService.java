package com.example.javasbtemp1.user.service;

import com.example.javasbtemp1.user.dto.CreateUserDto;
import com.example.javasbtemp1.user.dto.PatchUserDto;
import com.example.javasbtemp1.user.entity.User;
import com.example.javasbtemp1.user.exception.DuplicateEmailException;
import com.example.javasbtemp1.user.exception.UserNotFoundException;
import com.example.javasbtemp1.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@NonNull
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@NonNull
	public User findById(Long id) throws UserNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

	@NonNull
	public User create(CreateUserDto payload) throws DuplicateEmailException {
		String email = normalizeEmail(payload.getEmail());
		ensureEmailAvailable(email);

		User user = new User();
		user.setName(payload.getName().trim());
		user.setEmail(email);
		user.setAddress(trimToNull(payload.getAddress()));
		return userRepository.save(user);
	}

	@NonNull
	public User patch(Long id, PatchUserDto payload)
			throws UserNotFoundException, DuplicateEmailException {
		User user = findById(id);

		if (payload.getName() != null) {
			user.setName(requireNonBlank(payload.getName(), "name"));
		}
		if (payload.getEmail() != null) {
			String email = normalizeEmail(requireNonBlank(payload.getEmail(), "email"));
			if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email)) {
				throw new DuplicateEmailException(email);
			}
			user.setEmail(email);
		}
		if (payload.isAddressProvided()) {
			user.setAddress(trimToNull(payload.getAddress()));
		}

		return userRepository.save(user);
	}

	public void delete(Long id) throws UserNotFoundException {
		User user = findById(id);
		userRepository.delete(user);
	}

	private void ensureEmailAvailable(String email) throws DuplicateEmailException {
		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new DuplicateEmailException(email);
		}
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private String requireNonBlank(String value, String field) throws IllegalArgumentException {
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException(field + " must not be blank");
		}
		return value.trim();
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
