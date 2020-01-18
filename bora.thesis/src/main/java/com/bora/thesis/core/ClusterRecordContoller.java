package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
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
		List<TrajectoryRecord> alltrajectories = new ArrayList<TrajectoryRecord>();
		distinctMacAddresses.stream().forEach(x -> {
			List<SingleRecord> routes = this.singleRecordService.getByMacAddress(x);
			alltrajectories.add(this.singleRecordService.formTrajectoryByPointLocations(routes));
		});
		TrajectoryRecord test = this.clusterRecordService.getFurthiestRecord(alltrajectories);
		return "cluster-record-view";
	}
}
