package com.iiht.training.eloan.service.impl;

import java.util.ArrayList;
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
import com.iiht.training.eloan.exception.ClerkNotFoundException;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ClerkService;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository processingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allAppliedLoans() {
		// TODO Auto-generated method stub
		List<Loan> appliedLoans=this.loanRepository.findByStatus(0);
		
//		List<LoanOutputDto> listLoanData = appliedLoans.stream().map(loan-> {
//			Optional<Users> user = this.usersRepository.findById(loan.getCustomerId());
//			if(!user.isPresent()) {
//				throw new CustomerNotFoundException("customer not found");
//			}
//			ProcessingDto processingDto;
//			List<ProcessingInfo> processingInfo = this.processingInfoRepository.findByLoanAppId(loan.getId());
//			if(processingInfo.isEmpty()) {
//				processingDto = new ProcessingDto();
//			}else {
//				processingDto = this.covertInputProcessInfoEntityToDto(processingInfo.get(0));
//			}
//			SanctionOutputDto sanctionOutputDto;
//			List<SanctionInfo> sanctionInfo = this.sanctionInfoRepository.findByLoanAppId(loan.getId());
//			if(sanctionInfo.isEmpty()) {
//				sanctionOutputDto = new SanctionOutputDto();
//			}else {
//				sanctionOutputDto = this.covertInputSanctionEntityToDto(sanctionInfo.get(0));
//			}
//			LoanOutputDto loanOutputDto = new LoanOutputDto();
//			loanOutputDto.setCustomerId(loan.getCustomerId());
//			loanOutputDto.setLoanAppId(loan.getId());
//			loanOutputDto.setUserDto(this.covertInputEntityToDto(user.get()));
//			loanOutputDto.setLoanDto(this.covertInputEntityToDto(loan));
//			loanOutputDto.setProcessingDto(processingDto);
//			loanOutputDto.setSanctionOutputDto(sanctionOutputDto);
//			loanOutputDto.setRemark(loan.getRemark());
//			return loanOutputDto;
//		}).collect(Collectors.toList());
		
		List<LoanOutputDto> loanOutputDtoList = new ArrayList<>();
		for(int i=0;i<appliedLoans.size();i++) {
			Loan loan = appliedLoans.get(i);
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
			loanOutputDtoList.add(loanOutputDto);
		}
		
		return loanOutputDtoList;
	}

	@Override
	public ProcessingDto processLoan(Long clerkId, Long loanAppId, ProcessingDto processingDto) {
		// TODO Auto-generated method stub
		
		Optional<Users> user = this.usersRepository.findById(clerkId);
		if(!user.isPresent()) {
			throw new ClerkNotFoundException("clerk not found");
		}
		
		Optional<Loan> optionalLoan = this.loanRepository.findById(loanAppId);
		if(!optionalLoan.isPresent()) {
			throw new LoanNotFoundException("loan is not found");
		}
		Loan loan = optionalLoan.get();
		
		ProcessingInfo processInfo=this.covertInputProcessInfoDtoToProcessingInfoEntity(processingDto);
		
		processInfo.setLoanAppId(loanAppId);
		processInfo.setLoanClerkId(clerkId);
		this.processingInfoRepository.save(processInfo);
		loan.setStatus(1);
		this.loanRepository.save(loan);
		return processingDto;
	}
	
	
	private ProcessingInfo covertInputProcessInfoDtoToProcessingInfoEntity(ProcessingDto processDto) {
		ProcessingInfo processingInfo = new ProcessingInfo();
		processingInfo.setAcresOfLand(processDto.getAcresOfLand());
		processingInfo.setAddressOfProperty(processDto.getAddressOfProperty());
		processingInfo.setAppraisedBy(processDto.getAppraisedBy());
		processingInfo.setLandValue(processDto.getLandValue());
		processingInfo.setSuggestedAmountOfLoan(processDto.getSuggestedAmountOfLoan());
		processingInfo.setValuationDate(processDto.getValuationDate());
		return processingInfo;
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
	
	private SanctionOutputDto covertInputSanctionEntityToDto(SanctionInfo sanctionInfo) {
		SanctionOutputDto sanctionOutputDto = new SanctionOutputDto();
		sanctionOutputDto.setLoanAmountSanctioned(sanctionInfo.getLoanAmountSanctioned());
		sanctionOutputDto.setLoanClosureDate(sanctionInfo.getLoanClosureDate());
		sanctionOutputDto.setMonthlyPayment(sanctionInfo.getMonthlyPayment());
		sanctionOutputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
		sanctionOutputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
		return sanctionOutputDto;
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
