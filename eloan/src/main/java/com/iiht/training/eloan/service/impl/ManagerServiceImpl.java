package com.iiht.training.eloan.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.ClerkNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.exception.ManagerNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	@Override
	public List<LoanOutputDto> allProcessedLoans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RejectDto rejectLoan(Long managerId, Long loanAppId, RejectDto rejectDto) {
		// TODO Auto-generated method stub
		
		Optional<Users> user = this.usersRepository.findById(managerId);
		if(!user.isPresent()) {
			throw new ManagerNotFoundException("manager not found");
		}
		
		Optional<Loan> optionalLoan = this.loanRepository.findById(loanAppId);
		if(!optionalLoan.isPresent()) {
			throw new LoanNotFoundException("loan is not found");
		}
		Loan loan = optionalLoan.get();
		
		//this.covertInputProcessInfoDtoToProcessingInfoEntity(processingDto);
		
		/*
		 * processInfo.setLoanAppId(loanAppId); processInfo.setLoanClerkId(clerkId);
		 * this.processingInfoRepository.save(processInfo);
		 */
		loan.setStatus(1);
		loan.setRemark(rejectDto.getRemark());
		this.loanRepository.save(loan);
		return rejectDto;
		
	}

	@Override
	public SanctionOutputDto sanctionLoan(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		// TODO Auto-generated method stub
		
		
		Optional<Users> user = this.usersRepository.findById(managerId);
		if(!user.isPresent()) {
			throw new ManagerNotFoundException("manager not found");
		}
		
		Optional<Loan> optionalLoan = this.loanRepository.findById(loanAppId);
		if(!optionalLoan.isPresent()) {
			throw new LoanNotFoundException("loan is not found");
		}
		Loan loan = optionalLoan.get();
		
		SanctionInfo sanctionInfo=this.covertInputSanctionDtoToSanctionInfoEntity(sanctionDto);
		
//		Term payment amount = (Sanctioned loan amount ) * (1 + interest rate/100) ^ (term of loan)		
//		Monthly payment = (Term payment amount ) / (Term of loan)
		LocalDate startDate = LocalDate.parse(sanctionDto.getPaymentStartDate());
		String loanClosureDate = startDate.plusMonths(sanctionDto.getTermOfLoan().longValue()).toString();
		Double interestRate =7/100.0;
		Double monthlyRate = interestRate / 12.0;
		Double monthlyPayment = (sanctionDto.getLoanAmountSanctioned()*monthlyRate) / 
		            (1-Math.pow(1+monthlyRate, - sanctionDto.getTermOfLoan()));
//		Double termPaymentAmount = Math.pow((sanctionDto.getLoanAmountSanctioned()) * (1 + 7/100),sanctionDto.getTermOfLoan());
//		Double monthlyPayment = termPaymentAmount/ sanctionDto.getTermOfLoan();
		
		
		sanctionInfo.setLoanAppId(loanAppId);
		sanctionInfo.setManagerId(managerId);
		sanctionInfo.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		sanctionInfo.setTermOfLoan(sanctionDto.getTermOfLoan());
		sanctionInfo.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		sanctionInfo.setLoanClosureDate(loanClosureDate);
		sanctionInfo.setMonthlyPayment(monthlyPayment);
		this.sanctionInfoRepository.save(sanctionInfo);
		loan.setStatus(2);
		this.loanRepository.save(loan);
		SanctionOutputDto sanctionOutputDto = new SanctionOutputDto();
		sanctionOutputDto.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		sanctionOutputDto.setLoanClosureDate(loanClosureDate);
		sanctionOutputDto.setMonthlyPayment(monthlyPayment);
		sanctionOutputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
		sanctionOutputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
		return sanctionOutputDto;
	}
		//return null;



private SanctionInfo covertInputSanctionDtoToSanctionInfoEntity(SanctionDto sanctionDto) {
	SanctionInfo sanctionInfo = new SanctionInfo();
	sanctionInfo.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
	sanctionInfo.setTermOfLoan(sanctionDto.getTermOfLoan());
	sanctionInfo.setPaymentStartDate(sanctionDto.getPaymentStartDate());
	
	return sanctionInfo;
}

}
