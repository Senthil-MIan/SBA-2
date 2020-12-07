package com.iiht.training.eloan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.Users;

@Repository
public interface ProcessingInfoRepository extends JpaRepository<ProcessingInfo, Long>{
	List<ProcessingInfo> findByLoanAppId(Long appId);

}
