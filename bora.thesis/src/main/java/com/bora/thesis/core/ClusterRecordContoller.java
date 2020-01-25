package com.bora.thesis.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.service.ClusterRecordService;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class ClusterRecordContoller {

	@Autowired
	ClusterRecordService clusterRecordService;

	@Autowired
	SingleRecordService singleRecordService;

	@RequestMapping(value = "/cluster", method = RequestMethod.GET)
	public String doGetCluster(final Model model) {
		List<String> distinctMacAddresses = this.singleRecordService.getDistinctMacAdresses();
		List<TrajectoryRecord> alltrajectories = this.singleRecordService.generateAllTrajectories(distinctMacAddresses);
		ClusterRecord r = this.clusterRecordService.kMemberAlgorithm(alltrajectories, 5);
		// List<String> list = r.getTrajectories().stream().map(x ->
		// this.singleRecordService.translateToVisualisedTrajectory(x).getInicalTrajectory()).collect(Collectors.toList());
		// System.out.println(list);
		return "cluster-record-view";
	}
}