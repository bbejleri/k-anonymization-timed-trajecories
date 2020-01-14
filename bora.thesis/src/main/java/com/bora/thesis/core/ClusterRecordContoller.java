package com.bora.thesis.core;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author: bora
 */
@Controller
public class ClusterRecordContoller {

	@RequestMapping(value = "/cluster", method = RequestMethod.GET)
	public String doGetCluster(final Model model) {
		// TODO: Add view logic
		return "cluster-record-view";
	}
}
