package com.iiht.training.eloan.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
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
	private ProcessingInfoRepository processingInfoRepository;

	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;

	@Override
	public UserDto register(UserDto userDto) {
		System.out.println(userDto);
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
				userDto.getMobile().length() !=10) {
			throw new InvalidDataException("mobile number is not as per standard");
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
		loan.setStatus(0);
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
		Optional<Loan> optionalLoan = this.loanRepository.findById(loanAppId);
		if(!optionalLoan.isPresent()) {
			throw new LoanNotFoundException("loan is not found");
		}
		Loan loan = optionalLoan.get();
		Optional<Users> user = this.usersRepository.findById(loan.getCustomerId());
		if(!user.isPresent()) {
			throw new CustomerNotFoundException("customer not found");
		}
		ProcessingDto processingDto;
		List<ProcessingInfo> processingInfo = this.processingInfoRepository.findByLoanAppId(loan.getId());
		if(processingInfo.isEmpty()) {
			processingDto = new ProcessingDto();
		}else {
			processingDto = this.covertInputProcessInfoEntityToDto(processingInfo.get(0));
		}
		SanctionOutputDto sanctionOutputDto;
		List<SanctionInfo> sanctionInfo = this.sanctionInfoRepository.findByLoanAppId(loan.getId());
		if(sanctionInfo.isEmpty()) {
			sanctionOutputDto = new SanctionOutputDto();
		}else {
			sanctionOutputDto = this.covertInputSanctionEntityToDto(sanctionInfo.get(0));
		}

		LoanOutputDto loanOutputDto = new LoanOutputDto();
		loanOutputDto.setCustomerId(loan.getCustomerId());
		loanOutputDto.setLoanAppId(loan.getId());
		loanOutputDto.setUserDto(this.covertInputEntityToDto(user.get()));
		loanOutputDto.setLoanDto(this.covertInputEntityToDto(loan));
		loanOutputDto.setProcessingDto(processingDto);
		loanOutputDto.setSanctionOutputDto(sanctionOutputDto);
		loanOutputDto.setRemark(loan.getRemark());

		String status="";
		if(loan.getStatus()!=null)
		{
			switch(loan.getStatus()) {
			case 1:
				status = "Processed";
				break;
			case 2:
				status = "Sanctioned";
				break;
			case -1:
				status="Rejected";
				break;
			default: 
				status = "Applied";
				break;
			}
		}
		loanOutputDto.setStatus(status);
		return loanOutputDto;


	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) {
		// TODO Auto-generated method stub
		Optional<Users> user = this.usersRepository.findById(customerId);
		if(!user.isPresent()) {
			throw new CustomerNotFoundException("customer not found");
		}
		List<Loan> listLoans=this.loanRepository.findByCustomerId(customerId);
		if(listLoans.isEmpty()) {
			throw new LoanNotFoundException("loan is not found");
		}
		List<LoanOutputDto> listLoanData = listLoans.stream().map(loan-> {
			ProcessingDto processingDto;
			List<ProcessingInfo> processingInfo = this.processingInfoRepository.findByLoanAppId(loan.getId());
			if(processingInfo.isEmpty()) {
				processingDto = new ProcessingDto();
			}else {
				processingDto = this.covertInputProcessInfoEntityToDto(processingInfo.get(0));
			}
			SanctionOutputDto sanctionOutputDto;
			List<SanctionInfo> sanctionInfo = this.sanctionInfoRepository.findByLoanAppId(loan.getId());
			if(sanctionInfo.isEmpty()) {
				sanctionOutputDto = new SanctionOutputDto();
			}else {
				sanctionOutputDto = this.covertInputSanctionEntityToDto(sanctionInfo.get(0));
			}
			LoanOutputDto loanOutputDto = new LoanOutputDto();
			loanOutputDto.setCustomerId(loan.getCustomerId());
			loanOutputDto.setLoanAppId(loan.getId());
			loanOutputDto.setUserDto(this.covertInputEntityToDto(user.get()));
			loanOutputDto.setLoanDto(this.covertInputEntityToDto(loan));
			loanOutputDto.setProcessingDto(processingDto);
			loanOutputDto.setSanctionOutputDto(sanctionOutputDto);
			loanOutputDto.setRemark(loan.getRemark());
			return loanOutputDto;
		}).collect(Collectors.toList());		
		return listLoanData;
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
	/*
	 * private UserDto covertInputEntityToDto(Users user) { UserDto userDto = new
	 * UserDto(); userDto.setFirstName(user.getFirstName());
	 * userDto.setLastName(user.getLastName()); userDto.setMobile(user.getMobile());
	 * userDto.setEmail(user.getEmail()); userDto.setId(user.getId()); return
	 * userDto; }
	 */

	private Loan covertInputDtoToEntity(LoanDto loanDto) {
		Loan loan = new Loan();

		loan.setLoanName(loanDto.getLoanName());
		loan.setLoanAmount(loanDto.getLoanAmount());
		loan.setLoanApplicationDate(loanDto.getLoanApplicationDate());
		loan.setBusinessStructure(loanDto.getBusinessStructure());
		loan.setBillingIndicator(loanDto.getBillingIndicator());
		loan.setTaxIndicator(loanDto.getTaxIndicator());		
		return loan;
	}
	private LoanDto covertInputEntityToDto(Loan loan) {
		LoanDto loanDto = new LoanDto();
		loanDto.setLoanName(loan.getLoanName());
		loanDto.setLoanAmount(loan.getLoanAmount());
		loanDto.setLoanApplicationDate(loan.getLoanApplicationDate());
		loanDto.setBusinessStructure(loan.getBusinessStructure());
		loanDto.setBillingIndicator(loan.getBillingIndicator());
		loanDto.setTaxIndicator(loan.getTaxIndicator());

		return loanDto;
	}

	private ProcessingDto covertInputProcessInfoEntityToDto(ProcessingInfo processingInfo) {
		ProcessingDto processingDto = new ProcessingDto();
		processingDto.setAcresOfLand(processingInfo.getAcresOfLand());
		processingDto.setAddressOfProperty(processingInfo.getAddressOfProperty());
		processingDto.setAppraisedBy(processingInfo.getAppraisedBy());
		processingDto.setLandValue(processingInfo.getLandValue());
		processingDto.setSuggestedAmountOfLoan(processingInfo.getSuggestedAmountOfLoan());
		processingDto.setValuationDate(processingInfo.getValuationDate());
		return processingDto;
	}

	private SanctionOutputDto covertInputSanctionEntityToDto(SanctionInfo sanctionInfo) {
		SanctionOutputDto sanctionOutputDto = new SanctionOutputDto();
		sanctionOutputDto.setLoanAmountSanctioned(sanctionInfo.getLoanAmountSanctioned());
		sanctionOutputDto.setLoanClosureDate(sanctionInfo.getLoanClosureDate());
		sanctionOutputDto.setMonthlyPayment(sanctionInfo.getMonthlyPayment());
		sanctionOutputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
		sanctionOutputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
		return sanctionOutputDto;
	}



}
