package com.rainbowhospitals.builder;

import java.util.List;

import com.rainbowhospitals.dto.BedAssignmentHistoryDTO;
import com.rainbowhospitals.model.BedAssignmentHistory;

public class BedAssignmentHistoryBuilder {
	
	public static List<BedAssignmentHistoryDTO> buildBedHistoryDTOFromBedHistory(List<BedAssignmentHistory> bedHistory)
	{
		return bedHistory.stream().map(history->
					BedAssignmentHistoryDTO.builder()
					.BedAssignmentHistoryId(history.getBedAssignmentHistoryId())
					.bedNumber(history.getBed().getBedNumber())
					.assignedAt(history.getAssignedAt())
					.vacatedAt(history.getVacatedAt())
					.patientId(history.getPatientId())
					.build()).toList();
				
				
	}
	
	public static BedAssignmentHistoryDTO buildBedHistoryResponseDTOFromBedHistory(BedAssignmentHistory bedHistory){
		      return BedAssignmentHistoryDTO.builder()
		    		  .BedAssignmentHistoryId(bedHistory.getBedAssignmentHistoryId())
		    		  .bedNumber(bedHistory.getBed().getBedNumber())
		    		  .patientId(bedHistory.getPatientId())
		    		  .assignedAt(bedHistory.getAssignedAt())
		    		  .build();
	}
	
}
