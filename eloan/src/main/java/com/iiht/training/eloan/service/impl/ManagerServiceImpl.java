package com.iiht.training.eloan.service.impl;

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
		return null;
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
		
		SanctionInfo sanctionInfo;
		
		sanctionInfo.setLoanAppId(loanAppId);
		sanctionInfo.setManagerId(managerId);
		this.sanctionInfoRepository.save(sanctionInfo);
		loan.setStatus(1);
		this.loanRepository.save(loan);
		return sanctionOutputDto;
	}
		//return null;
}


private ProcessingInfo covertInputProcessInfoDtoToProcessingInfoEntity(SanctionDto sanctionDto) {
	ProcessingInfo processingInfo = new ProcessingInfo();
	processingInfo.setAcresOfLand(SanctionDto.getAcresOfLand());
	processingInfo.setAddressOfProperty(SanctionDto.getAddressOfProperty());
	processingInfo.setAppraisedBy(SanctionDto.getAppraisedBy());
	processingInfo.setLandValue(SanctionDto.getLandValue());
	processingInfo.setSuggestedAmountOfLoan(SanctionDto.getSuggestedAmountOfLoan());
	processingInfo.setValuationDate(SanctionDto.getValuationDate());
	return processingInfo;W
}

}
