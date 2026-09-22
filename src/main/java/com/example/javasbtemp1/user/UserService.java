package com.example.javasbtemp1.user;

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
	public User create(CreateUserRequest request) throws DuplicateEmailException {
		String email = normalizeEmail(request.getEmail());
		ensureEmailAvailable(email);

		User user = new User();
		user.setName(request.getName().trim());
		user.setEmail(email);
		user.setAddress(trimToNull(request.getAddress()));
		return userRepository.save(user);
	}

	@NonNull
	public User patch(Long id, UserPatchRequest request)
			throws UserNotFoundException, DuplicateEmailException {
		User user = findById(id);

		if (request.getName() != null) {
			user.setName(requireNonBlank(request.getName(), "name"));
		}
		if (request.getEmail() != null) {
			String email = normalizeEmail(requireNonBlank(request.getEmail(), "email"));
			if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email)) {
				throw new DuplicateEmailException(email);
			}
			user.setEmail(email);
		}
		if (request.isAddressProvided()) {
			user.setAddress(trimToNull(request.getAddress()));
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
