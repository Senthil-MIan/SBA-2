package com.iiht.training.eloan.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public UserDto register(UserDto userDto) {
		// TODO Auto-generated method stub
		if(userDto.getFirstName() == null || userDto.getFirstName().equals("")  && 
				userDto.getFirstName().length()< 3 || userDto.getFirstName().length()>100) {
			throw new InvalidDataException("firstname is not as per standard");
		}
		Users user = this.covertInputDtoToEntity(userDto);
		user.setRole("Customer");
		Users registerdCustomer = this.usersRepository.save(user);
		return this.covertInputEntityToDto(registerdCustomer);
		
	}

	@Override
	public LoanOutputDto applyLoan(Long customerId, LoanDto loanDto) {
		Optional<Users> user = this.usersRepository.findById(customerId);
		if(!user.isPresent()) {
			throw new CustomerNotFoundException("customer not found");
		}
		Loan loan = Loan.covertInputDtoToEntity(loanDto);
		loan.setCustomerId(customerId);		
		Loan insertedLoan = loanRepository.save(loan);
		
		LoanOutputDto loanOutputDto = new LoanOutputDto();
		loanOutputDto.setCustomerId(loan.getCustomerId());
		loanOutputDto.setLoanAppId(loan.getId());
		loanOutputDto.setUserDto(this.covertInputEntityToDto(user.get()));
		loanOutputDto.setLoanDto(Loan.covertInputEntityToDto(insertedLoan));
		return loanOutputDto;
	}

	@Override
	public LoanOutputDto getStatus(Long loanAppId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) {
		// TODO Auto-generated method stub
		return null;
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
