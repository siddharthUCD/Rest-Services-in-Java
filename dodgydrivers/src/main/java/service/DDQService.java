package service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.AbstractQuotationService;
import service.core.ClientInfo;
import service.core.Quotation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of Quotation Service for Dodgy Drivers Insurance Company
 *  
 * @author Rem
 *
 */
@RestController
public class DDQService extends AbstractQuotationService {
	// All references are to be prefixed with an DD (e.g. DD001000)
	private static final String PREFIX = "DD";
	private static final String COMPANY = "Dodgy Drivers Corp.";

	private Map<String, Quotation> quotations = new HashMap<>();

	@RequestMapping(value="/quotations",method= RequestMethod.POST)
	public ResponseEntity<Quotation> createQuotation(@RequestBody ClientInfo info) throws URISyntaxException {
		Quotation quotation = generateQuotation(info);
		quotations.put(quotation.getReference(), quotation);
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
				build().toUriString()+ "/quotations/"+quotation.getReference();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
	}

	@RequestMapping(value="/quotations/{reference}",method=RequestMethod.GET)
	public Quotation getResource(@PathVariable("reference") String reference) {
		Quotation quotation = quotations.get(reference);
		if (quotation == null) throw new NoSuchQuotationException();
		return quotation;
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public class NoSuchQuotationException extends RuntimeException {
		static final long serialVersionUID = -6516152229878843037L;
	}

	/**
	 * Quote generation:
	 * 5% discount per penalty point (3 points required for qualification)
	 * 50% penalty for <= 3 penalty points
	 * 10% discount per year no claims
	 */

	public Quotation generateQuotation(ClientInfo info) {
		// Create an initial quotation between 800 and 1000
		double price = generatePrice(800, 200);
		
		// 5% discount per penalty point (3 points required for qualification)
		int discount = (info.getPoints() > 3) ? 5*info.getPoints():-50;
		
		// Add a no claims discount
		discount += getNoClaimsDiscount(info);
		
		// Generate the quotations and send it back
		return new Quotation(COMPANY, generateReference(PREFIX), (price * (100-discount)) / 100);
	}

	private int getNoClaimsDiscount(ClientInfo info) {
		return 10*info.getNoClaims();
	}



}
