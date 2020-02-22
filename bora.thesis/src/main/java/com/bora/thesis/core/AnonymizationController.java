package com.bora.thesis.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bora.thesis.service.AnonymizationService;

/**
 * @author: bora
 */
@Controller
public class AnonymizationController {

	@Autowired
	private AnonymizationService anonymizationService;

	@RequestMapping(value = "/anonymize/data", method = RequestMethod.GET)
	public String anonymizeDataset(final Model model, @RequestParam(name = "k", required = false) int k) {
		try {
			this.anonymizationService.anonymizeDataset(k);
			model.addAttribute("k", k);
			return "anonymization-success";
		} catch (Exception e) {
			model.addAttribute("error", e.toString());
			return "anonymization-error";
		}
	}
}
