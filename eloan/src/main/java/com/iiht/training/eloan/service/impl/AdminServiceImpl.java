package com.iiht.training.eloan.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDto registerClerk(UserDto userDto) {
		// TODO Auto-generated method stub
		if(userDto.getFirstName() == null || userDto.getFirstName().equals("")  && 
				userDto.getFirstName().length()< 3 || userDto.getFirstName().length()>100) {
			throw new InvalidDataException("firstname is not as per standard");
		}
		else if(userDto.getLastName() == null || userDto.getLastName().equals("")  && 
				userDto.getLastName().length()< 3 || userDto.getLastName().length()>100) {
			throw new InvalidDataException("lastname is not as per standard");
		}
		else if(userDto.getEmail() == null || userDto.getEmail().equals("")  && 
				userDto.getEmail().length()< 3 || userDto.getEmail().length()>100 || !userDto.getEmail().contains("@")) {
			throw new InvalidDataException("email is not as per standard");
		}
		else if(userDto.getMobile() == null || userDto.getMobile().equals("")  && 
				userDto.getMobile() .length()< 10 || userDto.getMobile().length()>10) {
			throw new InvalidDataException("mobile number is not as per standard");
		}
		Users user = this.covertInputDtoToEntity(userDto);
		user.setRole("Clerk");
		Users registerdClerk = this.usersRepository.save(user);
		return this.covertInputEntityToDto(registerdClerk);
	}

	@Override
	public UserDto registerManager(UserDto userDto) {
		// TODO Auto-generated method stub
		if(userDto.getFirstName() == null || userDto.getFirstName().equals("")  || 
				userDto.getFirstName().length()< 3 || userDto.getFirstName().length()>100) {
			throw new InvalidDataException("firstname is not as per standard");
		}
		else if(userDto.getLastName() == null || userDto.getLastName().equals("")  ||
				userDto.getLastName().length()< 3 || userDto.getLastName().length()>100) {
			throw new InvalidDataException("lastname is not as per standard");
		}
		else if(userDto.getEmail() == null || userDto.getEmail().equals("")  || 
				userDto.getEmail().length()< 3 || userDto.getEmail().length()>100 || !userDto.getEmail().contains("@")) {
			throw new InvalidDataException("email is not as per standard");
		}
		else if(userDto.getMobile() == null || userDto.getMobile().equals("")  || 
				userDto.getMobile().length() < 10 || userDto.getMobile().length()>10) {
			throw new InvalidDataException("mobile number is not as per standard");
		}
		Users user = this.covertInputDtoToEntity(userDto);
		user.setRole("Manager");
		Users registerdManager = this.usersRepository.save(user);
		return this.covertInputEntityToDto(registerdManager);
	}

	@Override
	public List<UserDto> getAllClerks() {
		List<Users> clerkList = this.usersRepository.findByRole("Clerk");
		List<UserDto> userDtoList = clerkList.stream().map(user -> this.covertInputEntityToDto(user)).collect(Collectors.toList());
		return userDtoList;
	}

	@Override
	public List<UserDto> getAllManagers() {
		// TODO Auto-generated method stub
		List<Users> managerList = this.usersRepository.findByRole("Manager");
		List<UserDto> userDtoList = managerList.stream().map(user -> this.covertInputEntityToDto(user)).collect(Collectors.toList());
		return userDtoList;
	}
	
	private Users covertInputDtoToEntity(UserDto userDto) {
		Users user = new Users();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setMobile(userDto.getMobile());
		user.setEmail(userDto.getEmail());
		return user;
	}
	private UserDto covertInputEntityToDto(Users user) {
		UserDto userDto = new UserDto();
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setMobile(user.getMobile());
		userDto.setEmail(user.getEmail());
		userDto.setId(user.getId());
	
		return userDto;
	}

}
