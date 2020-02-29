package com.bora.thesis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.dataaccess.SingleRecordAnonymized;

/**
 * @author: bora
 */
@Service
public class AnonymizationService {

	@Autowired
	private ClusterRecordService clusterRecordService;

	@Autowired
	private SingleRecordAnonymizedService singleRecordAnonymizedService;

	public void anonymizeDataset(final int k) {
		List<ClusterRecord> clusters = this.clusterRecordService.finalizeKMember(k);
		List<SingleRecord> points = this.clusterRecordService.getAllClustersPoints(clusters);
		points.stream().forEach(point -> {
			SingleRecordAnonymized singleRecordAnonymized = this.singleRecordAnonymizedService.fillValuesSingleRecordAnonymized(point);
			this.singleRecordAnonymizedService.save(singleRecordAnonymized);
		});
	}
}
